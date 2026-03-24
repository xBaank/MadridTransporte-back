using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Train;

public class TrainClient : ITrainClient
{
    private readonly HttpClient _canoHttpClient;
    private readonly HttpClient _renfeHttpClient;
    private readonly IStopsService _stopsService;
    private readonly IRoutesService _routesService;
    private readonly ICrtmFallbackClient _crtmFallback;
    private readonly ILogger<TrainClient> _logger;

    public TrainClient(
        IHttpClientFactory httpClientFactory,
        IStopsService stopsService,
        IRoutesService routesService,
        ICrtmFallbackClient crtmFallback,
        ILogger<TrainClient> logger)
    {
        _canoHttpClient = httpClientFactory.CreateClient("ElCano");
        _renfeHttpClient = httpClientFactory.CreateClient("Renfe");
        _stopsService = stopsService;
        _routesService = routesService;
        _crtmFallback = crtmFallback;
        _logger = logger;
    }

    public async Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode)
    {
        var canoResult = await GetCanoTrainTimesAsync(fullStopCode);
        if (canoResult != null) return canoResult;

        // Fallback to CRTM and filter to train codMode only
        var crtmResult = await _crtmFallback.GetCrtmStopTimesAsync(fullStopCode);
        if (crtmResult?.Arrives == null) return crtmResult;

        crtmResult.Arrives = crtmResult.Arrives
            .Where(a => a.CodMode == int.Parse(CodeUtils.TrainCodMode))
            .ToList();
        return crtmResult;
    }

    private async Task<StopTimesDto?> GetCanoTrainTimesAsync(string fullStopCode)
    {
        try
        {
            var stationCode = await _stopsService.GetIdByStopCodeAsync(fullStopCode);
            if (stationCode == null) return null;

            var stopName = await _stopsService.GetStopNameByIdAsync(stationCode);
            var coordinates = await _stopsService.GetCoordinatesByStopCodeAsync(fullStopCode);
            var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);

            var body = new
            {
                commercialService = "YES",
                commercialStopType = "YES",
                page = new { pageNumber = 0 },
                stationCode,
                trafficType = "CERCANIAS",
            };

            var request = new HttpRequestMessage(HttpMethod.Post,
                "https://circulacion.api.adif.es/portroyalmanager/secure/circulationpaths/departures/traffictype/")
            {
                Content = JsonContent.Create(body),
            };
            request.Headers.TryAddWithoutValidation("User-Key", "f4ce9fbfa9d721e39b8984805901b5df");
            request.Headers.TryAddWithoutValidation("Host", "circulacion.api.adif.es");
            request.Headers.TryAddWithoutValidation("User-Agent", "okhttp/4.10.0");
            request.Headers.TryAddWithoutValidation("Connection", "Close");

            var response = await _canoHttpClient.SendAsync(request);
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound) return null;
            if (!response.IsSuccessStatusCode) return null;

            var json = await response.Content.ReadFromJsonAsync<JsonElement>();
            return await ExtractTrainStopTimes(json, coordinates, stopName, simpleStopCode);
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "El Cano train times failed for {StopCode}", fullStopCode);
            return null;
        }
    }

    private async Task<StopTimesDto> ExtractTrainStopTimes(JsonElement json, CoordinatesDto coordinates, string stopName, string simpleStopCode)
    {
        var paths = json.GetProperty("commercialPaths");
        var arrives = new List<ArriveDto>();

        foreach (var path in paths.EnumerateArray())
        {
            var departure = path.GetProperty("passthroughStep").GetProperty("departurePassthroughStepSides");
            var destinationCode = path.GetProperty("commercialPathInfo").GetProperty("commercialDestinationStationCode").GetString() ?? "";
            string destinationName;
            try { destinationName = await _stopsService.GetStopNameByIdAsync(destinationCode); }
            catch { destinationName = ""; }

            var line = path.GetProperty("commercialPathInfo").TryGetProperty("line", out var lineEl)
                ? lineEl.GetString() ?? "" : "";

            var delay = departure.GetProperty("forecastedOrAuditedDelay").GetInt32() * 1000L;
            var plannedTime = departure.GetProperty("plannedTime").GetInt64();
            var anden = departure.TryGetProperty("plannedPlatform", out var platEl)
                ? (int.TryParse(platEl.GetString(), out var p) ? p : (int?)null) : null;

            arrives.Add(new ArriveDto
            {
                Line = line,
                CodMode = int.Parse(CodeUtils.TrainCodMode),
                Anden = anden,
                Destination = destinationName,
                EstimatedArrive = plannedTime + delay,
            });
        }

        return new StopTimesDto
        {
            CodMode = int.Parse(CodeUtils.TrainCodMode),
            StopName = stopName,
            SimpleStopCode = simpleStopCode,
            Coordinates = coordinates,
            Arrives = GroupArrives(arrives),
            Incidents = [],
        };
    }

    public async Task<JsonElement?> GetRoutedTimesAsync(string originStopCode, string destinationStopCode)
    {
        try
        {
            var originFullCode = CodeUtils.CreateStopCode(CodeUtils.TrainCodMode, originStopCode);
            var destFullCode = CodeUtils.CreateStopCode(CodeUtils.TrainCodMode, destinationStopCode);

            var originStation = await _stopsService.GetIdByStopCodeAsync(originFullCode);
            var destStation = await _stopsService.GetIdByStopCodeAsync(destFullCode);

            if (originStation == null || destStation == null) return null;

            var madridNow = TimeUtils.GetMadridNow();
            var body = new
            {
                nucleo = "10",
                origen = originStation,
                destino = destStation,
                fchaViaje = madridNow.ToString("yyyyMMdd"),
                validaReglaNegocio = true,
                tiempoReal = true,
                servicioHorarios = "VTI",
                horaViajeOrigen = madridNow.ToString("HH:mm"),
                horaViajeLlegada = "26",
                accesibilidadTrenes = true,
            };

            var response = await _renfeHttpClient.PostAsJsonAsync("https://horarios.renfe.com/cer/HorariosServlet", body);
            if (!response.IsSuccessStatusCode) return null;

            var bytes = await response.Content.ReadAsByteArrayAsync();
            var text = Encoding.Latin1.GetString(bytes);
            var utf8Text = Encoding.UTF8.GetString(Encoding.Latin1.GetBytes(text));

            return JsonSerializer.Deserialize<JsonElement>(utf8Text);
        }
        catch (Exception ex)
        {
            _logger.LogWarning(ex, "Renfe routed times failed");
            return null;
        }
    }

    private static List<ArriveGroupDto> GroupArrives(List<ArriveDto> arrives)
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

public interface ICrtmFallbackClient
{
    Task<StopTimesDto?> GetCrtmStopTimesAsync(string fullStopCode);
}
