using System.Text.Json;
using MadridTransporte.Api.Clients.Crtm;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Bus;

public class BusClient(
    CrtmClient crtmClient,
    HttpClient httpClient,
    StopsService stopsService,
    RoutesService routesService,
    ILogger<BusClient> logger
)
{
    public async Task<StopTimesDto?> GetStopTimesAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var crtmTask = crtmClient.GetStopTimesAsync(fullStopCode, ct);
        var avanzaTask = GetAvanzaDataAsync(fullStopCode, ct);

        var coords = await stopsService.GetCoordinatesByStopCodeAsync(fullStopCode, ct);
        var name = await stopsService.GetStopNameByStopCodeAsync(fullStopCode, ct);

        await Task.WhenAll(crtmTask, avanzaTask);

        var crtmResult = await crtmTask;
        var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);

        var times =
            crtmResult
            ?? new StopTimesDto
            {
                CodMode = int.Parse(CodeUtils.BusCodMode),
                StopName = name,
                SimpleStopCode = simpleStopCode,
                Coordinates = coords,
                Arrives = null,
                Incidents = [],
            };

        // Update coordinates and name from DB
        times.Coordinates = coords;
        if (!string.IsNullOrEmpty(name))
            times.StopName = name;
        times.SimpleStopCode = simpleStopCode;

        // Merge Avanza data
        var avanzaArrives = await avanzaTask;
        if (avanzaArrives is not null && avanzaArrives.Count > 0)
        {
            if (times.Arrives is null)
            {
                times.Arrives = ArriveDto.GroupArrives(avanzaArrives);
            }
            else
            {
                var existingLines = times.Arrives.Select(a => a.Line).ToHashSet();
                var newLines = avanzaArrives.Select(a => a.Line).ToHashSet();
                if (!existingLines.Overlaps(newLines))
                {
                    var allArrives = avanzaArrives
                        .Concat(
                            times.Arrives.SelectMany(g =>
                                g.EstimatedArrives.Select(e => new ArriveDto
                                {
                                    Line = g.Line,
                                    LineCode = g.LineCode,
                                    Direction = g.Direction,
                                    CodMode = g.CodMode,
                                    Anden = g.Anden,
                                    Destination = g.Destination,
                                    EstimatedArrive = e,
                                })
                            )
                        )
                        .ToList();
                    times.Arrives = ArriveDto.GroupArrives(allArrives);
                }
            }
        }

        return times;
    }

    public async Task<StopTimesDto?> GetCrtmStopTimesAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        return await GetStopTimesAsync(fullStopCode, ct);
    }

    private async Task<List<ArriveDto>?> GetAvanzaDataAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var codMode = CodeUtils.GetCodModeFromFullStopCode(fullStopCode);
        if (codMode != CodeUtils.BusCodMode && codMode != CodeUtils.UrbanCodMode)
            return null;

        try
        {
            var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);
            var url =
                $"https://apisvt.avanzagrupo.com/lineas/getTraficosParada?empresa=25&parada={simpleStopCode}";
            var response = await httpClient.GetAsync(url, ct);
            if (!response.IsSuccessStatusCode)
                return null;

            var json = await response.Content.ReadFromJsonAsync<JsonElement>(ct);
            if (
                !json.TryGetProperty("data", out var data)
                || !data.TryGetProperty("traficos", out var traficos)
                || traficos.ValueKind != JsonValueKind.Array
            )
                return null;

            var madridNow = TimeUtils.GetMadridNow();
            var arrives = new List<ArriveDto>();

            foreach (var trafico in traficos.EnumerateArray())
            {
                var llegada = trafico.GetProperty("llegada").GetString();
                if (llegada is null)
                    continue;

                var hour = TimeOnly.Parse(llegada);
                var localDate = DateOnly.FromDateTime(madridNow.DateTime);
                var madridTime = new DateTimeOffset(
                    localDate.ToDateTime(hour),
                    TimeUtils.GetMadridTimeZone().GetUtcOffset(madridNow)
                );

                var line = trafico.GetProperty("coLineaWeb").GetString() ?? "";
                var route = await routesService.GetRouteAsync(
                    line,
                    [CodeUtils.BusCodMode, CodeUtils.UrbanCodMode],
                    ct
                );

                arrives.Add(
                    new ArriveDto
                    {
                        Line = line,
                        LineCode = route?.FullLineCode,
                        Destination =
                            "(Avanza) " + (trafico.GetProperty("dsDestino").GetString() ?? ""),
                        CodMode =
                            route is not null && int.TryParse(route.CodMode, out var cm)
                                ? cm
                                : int.Parse(CodeUtils.BusCodMode),
                        EstimatedArrive = madridTime.ToUnixTimeMilliseconds(),
                    }
                );
            }

            return arrives;
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Warning))
                logger.LogWarning(ex, "Avanza data fetch failed");
            return null;
        }
    }
}
