using System.Text.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Metro;

public class MetroClient(
    HttpClient httpClient,
    StopsService stopsService,
    RoutesService routesService,
    ILogger<MetroClient> logger
)
{
    private const string BaseUrl =
        "https://serviciosapp.metromadrid.es/servicios/rest/teleindicadores";

    public async Task<StopTimesDto?> GetStopTimesAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        try
        {
            var codigoEmpresa = await stopsService.GetIdByStopCodeAsync(fullStopCode, ct);
            if (codigoEmpresa == null)
                return null;

            var coordinates = await stopsService.GetCoordinatesByStopCodeAsync(fullStopCode, ct);
            var stopName = await stopsService.GetStopNameByIdAsync(codigoEmpresa, ct);
            var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);

            var request = new HttpRequestMessage(HttpMethod.Get, $"{BaseUrl}/{codigoEmpresa}");
            request.Headers.TryAddWithoutValidation("Accept", "application/json");

            var response = await httpClient.SendAsync(request, ct);
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                return null;
            if (!response.IsSuccessStatusCode)
                return CreateFailedTimes(stopName, coordinates, simpleStopCode);

            var json = await response.Content.ReadFromJsonAsync<JsonElement>(ct);
            if (
                !json.TryGetProperty("Vtelindicadores", out var indicators)
                || indicators.ValueKind != JsonValueKind.Array
            )
                return CreateFailedTimes(stopName, coordinates, simpleStopCode);

            return ExtractStopTimes(indicators, coordinates, stopName, simpleStopCode);
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "Metro times failed for {StopCode}", fullStopCode);
            return null;
        }
    }

    private StopTimesDto ExtractStopTimes(
        JsonElement indicators,
        CoordinatesDto coordinates,
        string stopName,
        string simpleStopCode
    )
    {
        var madridNow = TimeUtils.GetMadridNow();
        var arrives = new List<ArriveDto>();

        foreach (var indicator in indicators.EnumerateArray())
        {
            var proximo =
                indicator.TryGetProperty("proximo", out var p)
                && p.ValueKind == JsonValueKind.Number
                    ? p.GetInt64()
                    : (long?)null;
            var siguiente =
                indicator.TryGetProperty("siguiente", out var s)
                && s.ValueKind == JsonValueKind.Number
                    ? s.GetInt64()
                    : (long?)null;

            var emissionTimeStr = indicator.GetProperty("fechaHoraEmisionPrevision").GetString();
            if (emissionTimeStr == null)
                continue;

            var emissionTime = DateTimeOffset.Parse(emissionTimeStr);
            var diffMinutes = (long)(madridNow - emissionTime).TotalMinutes;

            var proximoDiff = proximo.HasValue ? proximo.Value - diffMinutes : (long?)null;
            var siguienteDiff = siguiente.HasValue ? siguiente.Value - diffMinutes : (long?)null;

            var line = indicator.GetProperty("linea").GetInt64().ToString();
            var route = routesService
                .GetRouteAsync(line, CodeUtils.MetroCodMode)
                .GetAwaiter()
                .GetResult();
            var lineCode =
                route?.FullLineCode ?? CodeUtils.CreateLineCode(CodeUtils.MetroCodMode, line);
            var destination = indicator.GetProperty("sentido").GetString() ?? "";
            int? anden =
                indicator.TryGetProperty("anden", out var andenEl)
                && andenEl.TryGetInt32(out var andenVal)
                    ? andenVal
                    : null;

            if (proximoDiff is >= 0)
            {
                var estimated = madridNow.AddMinutes(proximoDiff.Value + 1).AddSeconds(-1);
                arrives.Add(
                    new ArriveDto
                    {
                        Line = line,
                        LineCode = lineCode,
                        Destination = destination,
                        Anden = anden,
                        CodMode = int.Parse(CodeUtils.MetroCodMode),
                        EstimatedArrive = estimated.ToUnixTimeMilliseconds(),
                    }
                );
            }

            if (siguienteDiff is >= 0)
            {
                var estimated = madridNow.AddMinutes(siguienteDiff.Value + 1).AddSeconds(-1);
                arrives.Add(
                    new ArriveDto
                    {
                        Line = line,
                        LineCode = lineCode,
                        Destination = destination,
                        Anden = anden,
                        CodMode = int.Parse(CodeUtils.MetroCodMode),
                        EstimatedArrive = estimated.ToUnixTimeMilliseconds(),
                    }
                );
            }
        }

        return new StopTimesDto
        {
            CodMode = int.Parse(CodeUtils.MetroCodMode),
            StopName = stopName,
            SimpleStopCode = simpleStopCode,
            Coordinates = coordinates,
            Arrives = GroupArrives(arrives),
            Incidents = [],
        };
    }

    private static StopTimesDto CreateFailedTimes(
        string name,
        CoordinatesDto coords,
        string simpleStopCode
    ) =>
        new()
        {
            CodMode = int.Parse(CodeUtils.MetroCodMode),
            StopName = name,
            SimpleStopCode = simpleStopCode,
            Coordinates = coords,
            Arrives = null,
            Incidents = [],
        };

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

    private async Task<JsonElement> ReadFromJsonAsync<T>(HttpResponseMessage response)
    {
        return await response.Content.ReadFromJsonAsync<JsonElement>();
    }
}
