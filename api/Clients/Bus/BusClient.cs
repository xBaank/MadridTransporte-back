using System.Text.Json;
using MadridTransporte.Api.Clients.Crtm;
using MadridTransporte.Api.Clients.Train;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Bus;

public class BusClient(
    ICrtmClient crtmClient,
    HttpClient httpClient,
    IStopsService stopsService,
    IRoutesService routesService,
    ILogger<BusClient> logger) : IBusClient, ICrtmFallbackClient
{
    public async Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode)
    {
        var crtmTask = crtmClient.GetStopTimesAsync(fullStopCode);
        var avanzaTask = GetAvanzaDataAsync(fullStopCode);
        var coordsTask = stopsService.GetCoordinatesByStopCodeAsync(fullStopCode);
        var nameTask = stopsService.GetStopNameByStopCodeAsync(fullStopCode);

        await Task.WhenAll(crtmTask, avanzaTask, coordsTask, nameTask);

        var crtmResult = await crtmTask;
        var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);

        var times = crtmResult ?? new StopTimesDto
        {
            CodMode = int.Parse(CodeUtils.BusCodMode),
            StopName = await nameTask,
            SimpleStopCode = simpleStopCode,
            Coordinates = await coordsTask,
            Arrives = null,
            Incidents = [],
        };

        // Update coordinates and name from DB
        times.Coordinates = await coordsTask;
        if (!string.IsNullOrEmpty(await nameTask)) times.StopName = await nameTask;
        times.SimpleStopCode = simpleStopCode;

        // Merge Avanza data
        var avanzaArrives = await avanzaTask;
        if (avanzaArrives != null && avanzaArrives.Count > 0)
        {
            if (times.Arrives == null)
            {
                times.Arrives = GroupArrives(avanzaArrives);
            }
            else
            {
                var existingLines = times.Arrives.Select(a => a.Line).ToHashSet();
                var newLines = avanzaArrives.Select(a => a.Line).ToHashSet();
                if (!existingLines.Overlaps(newLines))
                {
                    var allArrives = avanzaArrives.Concat(
                        times.Arrives.SelectMany(g => g.EstimatedArrives.Select(e => new ArriveDto
                        {
                            Line = g.Line, LineCode = g.LineCode, Direction = g.Direction,
                            CodMode = g.CodMode, Anden = g.Anden, Destination = g.Destination,
                            EstimatedArrive = e,
                        }))).ToList();
                    times.Arrives = GroupArrives(allArrives);
                }
            }
        }

        return times;
    }

    public async Task<StopTimesDto?> GetCrtmStopTimesAsync(string fullStopCode)
    {
        return await GetStopTimesAsync(fullStopCode);
    }

    private async Task<List<ArriveDto>?> GetAvanzaDataAsync(string fullStopCode)
    {
        var codMode = CodeUtils.GetCodModeFromFullStopCode(fullStopCode);
        if (codMode != CodeUtils.BusCodMode && codMode != CodeUtils.UrbanCodMode) return null;

        try
        {
            var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);
            var url = $"https://apisvt.avanzagrupo.com/lineas/getTraficosParada?empresa=25&parada={simpleStopCode}";
            var response = await httpClient.GetAsync(url);
            if (!response.IsSuccessStatusCode) return null;

            var json = await response.Content.ReadFromJsonAsync<JsonElement>();
            if (!json.TryGetProperty("data", out var data) ||
                !data.TryGetProperty("traficos", out var traficos) ||
                traficos.ValueKind != JsonValueKind.Array)
                return null;

            var madridNow = TimeUtils.GetMadridNow();
            var arrives = new List<ArriveDto>();

            foreach (var trafico in traficos.EnumerateArray())
            {
                var llegada = trafico.GetProperty("llegada").GetString();
                if (llegada == null) continue;

                var hour = TimeOnly.Parse(llegada);
                var localDate = DateOnly.FromDateTime(madridNow.DateTime);
                var madridTime = new DateTimeOffset(localDate.ToDateTime(hour),
                    TimeUtils.GetMadridTimeZone().GetUtcOffset(madridNow));

                var line = trafico.GetProperty("coLineaWeb").GetString() ?? "";
                var route = await routesService.GetRouteAsync(line, [CodeUtils.BusCodMode, CodeUtils.UrbanCodMode]);

                arrives.Add(new ArriveDto
                {
                    Line = line,
                    LineCode = route?.FullLineCode,
                    Destination = "(Avanza) " + (trafico.GetProperty("dsDestino").GetString() ?? ""),
                    CodMode = route != null && int.TryParse(route.CodMode, out var cm) ? cm : int.Parse(CodeUtils.BusCodMode),
                    EstimatedArrive = madridTime.ToUnixTimeMilliseconds(),
                });
            }

            return arrives;
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "Avanza data fetch failed");
            return null;
        }
    }

    private static List<ArriveGroupDto> GroupArrives(IEnumerable<ArriveDto> arrives)
    {
        return arrives
            .OrderBy(a => int.TryParse(a.Line, out var n) ? n : int.MaxValue)
            .GroupBy(a => (a.Line, a.Destination, a.Anden))
            .Select(g => new ArriveGroupDto
            {
                CodMode = g.First().CodMode,
                Line = g.First().Line,
                LineCode = g.First().LineCode,
                Direction = g.First().Direction,
                Anden = g.First().Anden,
                Destination = g.First().Destination,
                EstimatedArrives = g.Select(a => a.EstimatedArrive).ToList(),
            })
            .ToList();
    }
}
