using System.Globalization;
using CsvHelper;
using CsvHelper.Configuration;
using EFCore.BulkExtensions;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Api.Utils;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Loader;

public class DataLoader(
    IServiceScopeFactory scopeFactory,
    HttpClient httpClient,
    ILogger<DataLoader> logger)
{
    private const int BatchSize = 50_000;

    private static readonly CsvConfiguration GtfsCsvConfig = new(CultureInfo.InvariantCulture)
    {
        HasHeaderRecord = true,
        MissingFieldFound = null,
        HeaderValidated = null,
        BadDataFound = null,
        TrimOptions = TrimOptions.Trim,
    };

    public async Task LoadDataAsync(CancellationToken ct = default)
    {
        var tempFiles = new List<string>();
        var tempDirs = new List<string>();

        try
        {
            logger.LogInformation("Starting data load");

            // Download GTFS zips and CSV files in parallel
            var gtfsDirsTask = DownloadAndExtractGtfs(tempFiles, tempDirs, ct);
            var csvFilesTask = DownloadCsvFiles(tempFiles, ct);
            await Task.WhenAll(gtfsDirsTask, csvFilesTask);
            var gtfsDirs = await gtfsDirsTask;
            var csvFiles = await csvFilesTask;

            await using var scope = scopeFactory.CreateAsyncScope();
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            db.Database.SetCommandTimeout(TimeSpan.FromMinutes(30));

            await TruncateAllTables(db, ct);
            await LoadStops(db, gtfsDirs, ct);
            await LoadRoutesFromGtfs(db, gtfsDirs, ct);
            await LoadRoutesFromCsv(db, csvFiles.TrainItineraries, ct);
            await LoadItinerariesFromGtfs(db, gtfsDirs, ct);
            await LoadItinerariesFromCsv(db, csvFiles.TrainItineraries, ct);
            await LoadShapes(db, gtfsDirs, ct);
            await LoadStopsInfo(db, csvFiles.StopsInfoFiles, ct);
            await LoadCalendars(db, gtfsDirs, ct);

            // StopOrders need the repeated metro stops map, so load after stops
            var repeatedToOriginalStops = await GetRepeatedMetroStops(db, ct);
            await LoadStopOrdersFromGtfs(db, gtfsDirs, repeatedToOriginalStops, ct);
            await LoadStopOrdersFromCsv(db, csvFiles.TrainItineraries, ct);

            logger.LogInformation("Data load completed successfully");
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Error loading data");
            throw;
        }
        finally
        {
            FileUtils.CleanupTempFiles(tempFiles, tempDirs);
        }
    }

    private async Task<List<GtfsDirInfo>> DownloadAndExtractGtfs(List<string> tempFiles, List<string> tempDirs,
        CancellationToken ct)
    {
        var downloadTasks = DataSourceConfig.AllGtfsFeeds.Select(async feed =>
        {
            var zipFile = await FileUtils.DownloadToTempFileAsync(httpClient, feed.Url, logger);
            lock (tempFiles) tempFiles.Add(zipFile);
            var dir = FileUtils.UnzipToTempDirectory(zipFile);
            lock (tempDirs) tempDirs.Add(dir);
            return new GtfsDirInfo(dir, feed.CodMode);
        });

        var feeds = await Task.WhenAll(downloadTasks);
        return feeds.ToList();
    }

    private record GtfsDirInfo(string Dir, int CodMode);

    private async Task<CsvFileSet> DownloadCsvFiles(List<string> tempFiles, CancellationToken ct)
    {
        var stopsInfoTasks = DataSourceConfig.StopsInfoUrls.Select(async url =>
        {
            var file = await FileUtils.DownloadToTempFileAsync(httpClient, url, logger);
            lock (tempFiles) tempFiles.Add(file);
            return file;
        });

        var trainItinFile = await FileUtils.DownloadToTempFileAsync(httpClient, DataSourceConfig.TrainItinerariesUrl,
            logger);
        tempFiles.Add(trainItinFile);

        var stopsInfoFiles = await Task.WhenAll(stopsInfoTasks);

        return new CsvFileSet
        {
            StopsInfoFiles = stopsInfoFiles.ToList(),
            TrainItineraries = trainItinFile,
        };
    }

    private static async Task TruncateAllTables(AppDbContext db, CancellationToken ct)
    {
        await db.Database.ExecuteSqlRawAsync(
            """
            TRUNCATE TABLE "Stops", "Routes", "Itineraries", "StopOrders", "Calendars", "Shapes", "StopInfos" CASCADE
            """, ct);
    }

    private async Task LoadStops(AppDbContext db, List<GtfsDirInfo> gtfsFeeds, CancellationToken ct)
    {
        logger.LogInformation("Loading stops");

        var seenStopIds = new HashSet<string>();
        var uniqueMetroNames = new HashSet<string>();
        var count = 0;

        foreach (var (dir, feedCodMode) in gtfsFeeds)
        {
            var filePath = Path.Combine(dir, "stops.txt");
            if (!File.Exists(filePath)) continue;

            using var stream = File.OpenRead(filePath);
            await foreach (var batch in ReadCsvChunks(stream, BatchSize))
            {
                var stops = new List<Stop>();
                foreach (var record in batch)
                {
                    var stopId = record.GetValueOrDefault("stop_id", "");
                    if (!stopId.Contains("par") && !int.TryParse(stopId, out _)) continue;
                    if (!seenStopIds.Add(stopId)) continue;

                    var stop = GtfsParsers.ParseStop(record, feedCodMode, logger);
                    if (stop == null) continue;

                    if (stop.CodMode.ToString() == CodeUtils.MetroCodMode)
                    {
                        if (!uniqueMetroNames.Add(stop.StopName)) continue;
                    }

                    stops.Add(stop);
                }

                if (stops.Count > 0)
                {
                    await BulkInsertAsync(db, stops, ct);
                    count += stops.Count;
                }
            }
        }

        logger.LogInformation("Loaded {Count} stops", count);
    }

    private async Task<Dictionary<string, string>> GetRepeatedMetroStops(AppDbContext db, CancellationToken ct)
    {
        var metroStops = await db.Stops
            .Where(s => s.CodMode.ToString() == CodeUtils.MetroCodMode)
            .Select(s => new { s.FullStopCode, s.StopName })
            .ToListAsync(ct);

        var nameToOriginal = new Dictionary<string, string>();
        var repeatedToOriginal = new Dictionary<string, string>();

        foreach (var stop in metroStops)
        {
            if (!nameToOriginal.TryAdd(stop.StopName, stop.FullStopCode))
            {
                repeatedToOriginal[stop.FullStopCode] = nameToOriginal[stop.StopName];
            }
        }

        return repeatedToOriginal;
    }

    private async Task LoadRoutesFromGtfs(AppDbContext db, List<GtfsDirInfo> gtfsFeeds, CancellationToken ct)
    {
        logger.LogInformation("Loading routes from GTFS");

        var seenRouteIds = new HashSet<string>();
        var count = 0;

        using var stream = FileUtils.CombineGtfsFiles("routes.txt", gtfsFeeds.Select(f => f.Dir).ToList());

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var routes = new List<TransitRoute>();
            foreach (var record in batch)
            {
                var routeId = record.GetValueOrDefault("route_id", "");
                if (!seenRouteIds.Add(routeId)) continue;

                var route = GtfsParsers.ParseRouteFromGtfs(record, logger);
                if (route != null) routes.Add(route);
            }

            if (routes.Count > 0)
            {
                await BulkInsertAsync(db, routes, ct);
                count += routes.Count;
            }
        }

        logger.LogInformation("Loaded {Count} routes from GTFS", count);
    }

    private async Task LoadRoutesFromCsv(AppDbContext db, string trainItinerariesPath, CancellationToken ct)
    {
        logger.LogInformation("Loading routes from CSV");

        var seenLineIds = new HashSet<string>();
        var count = 0;

        using var stream = File.OpenRead(trainItinerariesPath);

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var routes = new List<TransitRoute>();
            foreach (var record in batch)
            {
                var lineId = record.GetValueOrDefault("IDFLINEA", "");
                if (!seenLineIds.Add(lineId)) continue;

                var route = GtfsParsers.ParseRouteFromCsv(record, logger);
                if (route != null) routes.Add(route);
            }

            if (routes.Count > 0)
            {
                await BulkInsertAsync(db, routes, ct);
                count += routes.Count;
            }
        }

        logger.LogInformation("Loaded {Count} routes from CSV", count);
    }

    private async Task LoadItinerariesFromGtfs(AppDbContext db, List<GtfsDirInfo> gtfsFeeds, CancellationToken ct)
    {
        logger.LogInformation("Loading itineraries from GTFS");

        var count = 0;
        using var stream = FileUtils.CombineGtfsFiles("trips.txt", gtfsFeeds.Select(f => f.Dir).ToList());

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var itineraries = batch
                .Select(r => GtfsParsers.ParseItineraryFromGtfs(r, logger))
                .Where(i => i != null)
                .Cast<Itinerary>()
                .ToList();

            if (itineraries.Count > 0)
            {
                await BulkInsertAsync(db, itineraries, ct);
                count += itineraries.Count;
            }
        }

        logger.LogInformation("Loaded {Count} itineraries from GTFS", count);
    }

    private async Task LoadItinerariesFromCsv(AppDbContext db, string trainItinerariesPath, CancellationToken ct)
    {
        logger.LogInformation("Loading itineraries from CSV");

        var count = 0;
        using var stream = File.OpenRead(trainItinerariesPath);

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var itineraries = batch
                .Select(r => GtfsParsers.ParseItineraryFromCsv(r, logger))
                .Where(i => i != null)
                .Cast<Itinerary>()
                .ToList();

            if (itineraries.Count > 0)
            {
                await BulkInsertAsync(db, itineraries, ct);
                count += itineraries.Count;
            }
        }

        logger.LogInformation("Loaded {Count} itineraries from CSV", count);
    }

    private async Task LoadShapes(AppDbContext db, List<GtfsDirInfo> gtfsFeeds, CancellationToken ct)
    {
        logger.LogInformation("Loading shapes");

        var count = 0;
        using var stream = FileUtils.CombineGtfsFiles("shapes.txt", gtfsFeeds.Select(f => f.Dir).ToList());

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var shapes = batch
                .Select(r => GtfsParsers.ParseShape(r, logger))
                .Where(s => s != null)
                .Cast<Shape>()
                .ToList();

            if (shapes.Count > 0)
            {
                await BulkInsertAsync(db, shapes, ct);
                count += shapes.Count;
            }
        }

        logger.LogInformation("Loaded {Count} shapes", count);
    }

    private async Task LoadStopsInfo(AppDbContext db, List<string> stopsInfoFiles, CancellationToken ct)
    {
        logger.LogInformation("Loading stops info");

        var seen = new HashSet<(string, string)>();
        var count = 0;

        using var stream = FileUtils.CombineCsvFiles(stopsInfoFiles);

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var infos = new List<StopInfo>();
            foreach (var record in batch)
            {
                var info = GtfsParsers.ParseStopInfo(record, logger);
                if (info != null && seen.Add((info.IdEstacion, info.CodigoEmpresa)))
                    infos.Add(info);
            }

            if (infos.Count > 0)
            {
                await BulkInsertAsync(db, infos, ct);
                count += infos.Count;
            }
        }

        logger.LogInformation("Loaded {Count} stop infos", count);
    }

    private async Task LoadStopOrdersFromGtfs(AppDbContext db, List<GtfsDirInfo> gtfsFeeds,
        Dictionary<string, string> repeatedToOriginalStops, CancellationToken ct)
    {
        logger.LogInformation("Loading stop orders from GTFS");

        var count = 0;

        foreach (var (dir, feedCodMode) in gtfsFeeds)
        {
            var filePath = Path.Combine(dir, "stop_times.txt");
            if (!File.Exists(filePath)) continue;

            using var stream = File.OpenRead(filePath);
            await foreach (var batch in ReadCsvChunks(stream, BatchSize))
            {
                var stopOrders = new List<StopOrder>();
                foreach (var record in batch)
                {
                    var so = GtfsParsers.ParseStopOrderFromGtfs(record, feedCodMode, logger);
                    if (so == null) continue;

                    if (repeatedToOriginalStops.TryGetValue(so.FullStopCode, out var original))
                        so.FullStopCode = original;

                    stopOrders.Add(so);
                }

                if (stopOrders.Count > 0)
                {
                    await BulkInsertAsync(db, stopOrders, ct);
                    count += stopOrders.Count;
                }
            }
        }

        logger.LogInformation("Loaded {Count} stop orders from GTFS", count);
    }

    private async Task LoadStopOrdersFromCsv(AppDbContext db, string trainItinerariesPath, CancellationToken ct)
    {
        logger.LogInformation("Loading stop orders from CSV");

        var count = 0;
        using var stream = File.OpenRead(trainItinerariesPath);

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var stopOrders = batch
                .Select(r => GtfsParsers.ParseStopOrderFromCsv(r, logger))
                .Where(so => so != null)
                .Cast<StopOrder>()
                .ToList();

            if (stopOrders.Count > 0)
            {
                await BulkInsertAsync(db, stopOrders, ct);
                count += stopOrders.Count;
            }
        }

        logger.LogInformation("Loaded {Count} stop orders from CSV", count);
    }

    private async Task LoadCalendars(AppDbContext db, List<GtfsDirInfo> gtfsFeeds, CancellationToken ct)
    {
        logger.LogInformation("Loading calendars");

        var count = 0;
        using var stream = FileUtils.CombineGtfsFiles("calendar.txt", gtfsFeeds.Select(f => f.Dir).ToList());

        await foreach (var batch in ReadCsvChunks(stream, BatchSize))
        {
            var calendars = batch
                .Select(r => GtfsParsers.ParseCalendar(r, logger))
                .Where(c => c != null)
                .Cast<Data.Entities.Calendar>()
                .ToList();

            if (calendars.Count > 0)
            {
                await BulkInsertAsync(db, calendars, ct);
                count += calendars.Count;
            }
        }

        logger.LogInformation("Loaded {Count} calendars", count);
    }

    private async Task BulkInsertAsync<T>(AppDbContext db, List<T> items, CancellationToken ct) where T : class
    {
        try
        {
            await db.BulkInsertAsync(items, cancellationToken: ct);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "BulkInsert failed for {Type} ({Count} records): {Message} | Inner: {Inner}",
                typeof(T).Name, items.Count, ex.Message, ex.InnerException?.ToString() ?? "none");
            throw;
        }
    }

    private static async IAsyncEnumerable<List<Dictionary<string, string>>> ReadCsvChunks(Stream stream, int chunkSize)
    {
        using var reader = new StreamReader(stream);
        using var csv = new CsvReader(reader, GtfsCsvConfig);

        await csv.ReadAsync();
        csv.ReadHeader();
        var headers = csv.HeaderRecord ?? [];

        var chunk = new List<Dictionary<string, string>>(chunkSize);

        while (await csv.ReadAsync())
        {
            var dict = new Dictionary<string, string>();
            foreach (var header in headers)
            {
                var value = csv.GetField(header);
                if (value != null && !dict.ContainsKey(header))
                    dict[header] = value;
            }

            chunk.Add(dict);

            if (chunk.Count >= chunkSize)
            {
                yield return chunk;
                chunk = new List<Dictionary<string, string>>(chunkSize);
            }
        }

        if (chunk.Count > 0)
            yield return chunk;
    }

    private record CsvFileSet
    {
        public required List<string> StopsInfoFiles { get; init; }
        public required string TrainItineraries { get; init; }
    }
}
