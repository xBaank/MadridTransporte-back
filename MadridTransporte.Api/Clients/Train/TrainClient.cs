using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using MadridTransporte.Api.Clients.Bus;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Train;

public class TrainClient
{
    private readonly HttpClient _canoHttpClient;
    private readonly HttpClient _renfeHttpClient;
    private readonly StopsService _stopsService;
    private readonly RoutesService _routesService;
    private readonly BusClient _crtmFallback;
    private readonly ILogger<TrainClient> _logger;

    public TrainClient(
        IHttpClientFactory httpClientFactory,
        StopsService stopsService,
        RoutesService routesService,
        BusClient crtmFallback,
        ILogger<TrainClient> logger
    )
    {
        _canoHttpClient = httpClientFactory.CreateClient("ElCano");
        _renfeHttpClient = httpClientFactory.CreateClient("Renfe");
        _stopsService = stopsService;
        _routesService = routesService;
        _crtmFallback = crtmFallback;
        _logger = logger;
    }

    public async Task<StopTimesDto?> GetStopTimesAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var canoResult = await GetCanoTrainTimesAsync(fullStopCode, ct);
        if (canoResult is not null)
            return canoResult;

        // Fallback to CRTM and filter to train codMode only
        var crtmResult = await _crtmFallback.GetCrtmStopTimesAsync(fullStopCode, ct);
        if (crtmResult?.Arrives is null)
            return crtmResult;

        crtmResult.Arrives = crtmResult
            .Arrives.Where(a => a.CodMode == int.Parse(CodeUtils.TrainCodMode))
            .ToList();
        return crtmResult;
    }

    private async Task<StopTimesDto?> GetCanoTrainTimesAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        try
        {
            var stationCode = await _stopsService.GetIdByStopCodeAsync(fullStopCode, ct);
            if (stationCode is null)
                return null;

            var stopName = await _stopsService.GetStopNameByIdAsync(stationCode, ct);
            var coordinates = await _stopsService.GetCoordinatesByStopCodeAsync(fullStopCode, ct);
            var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);

            var body = new
            {
                commercialService = "YES",
                commercialStopType = "YES",
                page = new { pageNumber = 0 },
                stationCode,
                trafficType = "CERCANIAS",
            };

            var bodyJson = JsonSerializer.Serialize(body);
            var content = new StringContent(bodyJson);
            content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(
                "application/json"
            );

            var request = new HttpRequestMessage(
                HttpMethod.Post,
                "https://circulacion.api.adif.es/portroyalmanager/secure/circulationpaths/departures/traffictype/"
            )
            {
                Content = content,
            };
            request.Headers.TryAddWithoutValidation("User-Key", "f4ce9fbfa9d721e39b8984805901b5df");
            request.Headers.TryAddWithoutValidation("Host", "circulacion.api.adif.es");
            request.Headers.TryAddWithoutValidation("User-Agent", "okhttp/4.10.0");
            request.Headers.TryAddWithoutValidation("Connection", "Close");

            var response = await _canoHttpClient.SendAsync(request, ct);
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                return null;
            if (!response.IsSuccessStatusCode)
                return null;

            var json = await response.Content.ReadFromJsonAsync<JsonElement>(ct);
            return await ExtractTrainStopTimes(json, coordinates, stopName, simpleStopCode, ct);
        }
        catch (Exception ex)
        {
            if (_logger.IsEnabled(LogLevel.Warning))
                _logger.LogWarning(ex, "El Cano train times failed for {StopCode}", fullStopCode);
            return null;
        }
    }

    private async Task<StopTimesDto> ExtractTrainStopTimes(
        JsonElement json,
        CoordinatesDto coordinates,
        string stopName,
        string simpleStopCode,
        CancellationToken ct = default
    )
    {
        var paths = json.GetProperty("commercialPaths");
        var arrives = new List<ArriveDto>();

        foreach (var path in paths.EnumerateArray())
        {
            var departure = path.GetProperty("passthroughStep")
                .GetProperty("departurePassthroughStepSides");
            var destinationCode =
                path.GetProperty("commercialPathInfo")
                    .GetProperty("commercialDestinationStationCode")
                    .GetString()
                ?? "";
            string destinationName;
            try
            {
                destinationName = await _stopsService.GetStopNameByIdAsync(destinationCode, ct);
            }
            catch
            {
                destinationName = "";
            }

            var line = path.GetProperty("commercialPathInfo").TryGetProperty("line", out var lineEl)
                ? lineEl.GetString() ?? ""
                : "";

            var delay = departure.GetProperty("forecastedOrAuditedDelay").GetInt32() * 1000L;
            var plannedTime = departure.GetProperty("plannedTime").GetInt64();
            var anden = departure.TryGetProperty("plannedPlatform", out var platEl)
                ? (int.TryParse(platEl.GetString(), out var p) ? p : (int?)null)
                : null;

            arrives.Add(
                new ArriveDto
                {
                    Line = line,
                    CodMode = int.Parse(CodeUtils.TrainCodMode),
                    Anden = anden,
                    Destination = destinationName,
                    EstimatedArrive = plannedTime + delay,
                }
            );
        }

        return new StopTimesDto
        {
            CodMode = int.Parse(CodeUtils.TrainCodMode),
            StopName = stopName,
            SimpleStopCode = simpleStopCode,
            Coordinates = coordinates,
            Arrives = ArriveDto.GroupArrives(arrives),
            Incidents = [],
        };
    }

    public async Task<JsonElement?> GetRoutedTimesAsync(
        string originStopCode,
        string destinationStopCode,
        CancellationToken ct = default
    )
    {
        try
        {
            var originFullCode = CodeUtils.CreateStopCode(CodeUtils.TrainCodMode, originStopCode);
            var destFullCode = CodeUtils.CreateStopCode(
                CodeUtils.TrainCodMode,
                destinationStopCode
            );

            var originStation = await _stopsService.GetIdByStopCodeAsync(originFullCode, ct);
            var destStation = await _stopsService.GetIdByStopCodeAsync(destFullCode, ct);

            if (originStation is null || destStation is null)
                return null;

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

            var bodyJson = JsonSerializer.Serialize(body);
            var content = new StringContent(bodyJson);
            content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(
                "application/json"
            );

            var request = new HttpRequestMessage(
                HttpMethod.Post,
                "https://horarios.renfe.com/cer/HorariosServlet"
            )
            {
                Content = content,
            };
            request.Headers.TryAddWithoutValidation(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:64.0) Gecko/20100101 Firefox/64.0"
            );

            var response = await _renfeHttpClient.SendAsync(request, ct);
            if (!response.IsSuccessStatusCode)
                return null;

            var bytes = await response.Content.ReadAsByteArrayAsync(ct);
            var text = Encoding.Latin1.GetString(bytes);

            return JsonSerializer.Deserialize<JsonElement>(text);
        }
        catch (Exception ex)
        {
            if (_logger.IsEnabled(LogLevel.Warning))
                _logger.LogWarning(ex, "Renfe routed times failed");
            return null;
        }
    }
}
