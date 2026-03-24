using System.Net.Http.Json;
using System.Text.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Exceptions;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Emt;

public class EmtClient(HttpClient httpClient, StopsService stopsService, RoutesService routesService, ILogger<EmtClient> logger)
{
    private string? _accessToken;
    private readonly SemaphoreSlim _loginLock = new(1, 1);

    private async Task LoginAsync(CancellationToken ct = default)
    {
        await _loginLock.WaitAsync(ct);
        try
        {
            var request = new HttpRequestMessage(HttpMethod.Get, "https://openapi.emtmadrid.es/v1/mobilitylabs/user/login/");
            request.Headers.TryAddWithoutValidation("passKey", "504fea88211f2f90633f964189b7696037d65cc3a5f47b8fa1d5ea5e34db0239ad2e068851e72be0cec125779224749e3bc236c1b7af39d8a3d398e99223f058");
            request.Headers.TryAddWithoutValidation("X-ClientId", "428B01E6-693C-4F7F-A11E-3BB923420587");

            var response = await httpClient.SendAsync(request, ct);
            if (!response.IsSuccessStatusCode) throw new InvalidOperationException("EMT login failed");

            var json = await response.Content.ReadFromJsonAsync<JsonElement>(ct);
            _accessToken = json.GetProperty("data").EnumerateArray().First()
                .GetProperty("accessToken").GetString();
        }
        finally
        {
            _loginLock.Release();
        }
    }

    public async Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode, CancellationToken ct = default)
    {
        var simpleStopCode = CodeUtils.GetStopCodeFromFullStopCode(fullStopCode);

        if (_accessToken == null)
            await LoginAsync(ct);

        for (var tries = 3; tries > 0; tries--)
        {
            try
            {
                var url = $"https://openapi.emtmadrid.es/v2/transport/busemtmad/stops/{simpleStopCode}/arrives/";
                var body = new
                {
                    cultureInfo = "ES",
                    Text_StopRequired_YN = "Y",
                    Text_EstimationsRequired_YN = "Y",
                    Text_IncidencesRequired_YN = "Y",
                    DateTime_Referenced_Incidencies_YYYYMMDD = DateTime.UtcNow.ToString("yyyy-MM-dd"),
                };

                var bodyJson = JsonSerializer.Serialize(body);
                var content = new StringContent(bodyJson);
                content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue("application/json");

                var request = new HttpRequestMessage(HttpMethod.Post, url) { Content = content };
                request.Headers.TryAddWithoutValidation("accessToken", _accessToken);

                var response = await httpClient.SendAsync(request, ct);

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                    throw ApiException.NotFound("Stop not found");

                if (response.StatusCode is System.Net.HttpStatusCode.Unauthorized or System.Net.HttpStatusCode.Forbidden)
                {
                    await LoginAsync(ct);
                    continue;
                }

                if (!response.IsSuccessStatusCode) continue;

                var json = await response.Content.ReadFromJsonAsync<JsonElement>(ct);
                return ExtractStopTimes(json, simpleStopCode);
            }
            catch (ApiException) { throw; }
            catch (Exception ex)
            {
                logger.LogWarning(ex, "EMT request failed, {Tries} tries left", tries - 1);
            }
        }

        // Failed all tries — return unavailable
        try
        {
            var name = await stopsService.GetStopNameByStopCodeAsync(fullStopCode, ct);
            var coords = await stopsService.GetCoordinatesByStopCodeAsync(fullStopCode, ct);
            return new StopTimesDto
            {
                CodMode = int.Parse(CodeUtils.EmtCodMode),
                StopName = name,
                SimpleStopCode = simpleStopCode,
                Coordinates = coords,
                Arrives = null,
                Incidents = [],
            };
        }
        catch
        {
            return null;
        }
    }

    private StopTimesDto ExtractStopTimes(JsonElement json, string simpleStopCode)
    {
        var code = json.TryGetProperty("code", out var c) ? c.GetString() : null;
        if (code != "00" && code != "01")
        {
            var description = json.TryGetProperty("description", out var d) ? d.GetString() : null;
            throw new InvalidOperationException($"EMT error {code}: {description}");
        }

        var dataArr = json.GetProperty("data").EnumerateArray().ToList();
        if (dataArr.Count == 0)
            throw new InvalidOperationException("EMT returned empty data");

        var data = dataArr[0];

        var stopInfoArr = data.GetProperty("StopInfo").EnumerateArray().ToList();
        if (stopInfoArr.Count == 0)
            throw new InvalidOperationException("EMT returned empty StopInfo");
        var stopInfo = stopInfoArr[0];
        var stopName = stopInfo.GetProperty("stopName").GetString() ?? "";
        var geoCoords = stopInfo.GetProperty("geometry").GetProperty("coordinates");
        var coordinates = new CoordinatesDto
        {
            Latitude = geoCoords.EnumerateArray().ElementAt(1).GetDouble(),
            Longitude = geoCoords.EnumerateArray().ElementAt(0).GetDouble(),
        };

        List<ArriveDto>? arrives = null;
        if (data.TryGetProperty("Arrive", out var arriveArr) && arriveArr.ValueKind == JsonValueKind.Array)
        {
            arrives = [];
            foreach (var a in arriveArr.EnumerateArray())
            {
                var secondsToArrive = a.GetProperty("estimateArrive").GetInt64();
                var estimatedArrive = DateTimeOffset.UtcNow.AddSeconds(secondsToArrive).ToUnixTimeMilliseconds();
                var line = a.GetProperty("line").GetString() ?? "";
                var route = routesService.GetRouteAsync(line, CodeUtils.EmtCodMode).GetAwaiter().GetResult();
                var lineCode = route?.FullLineCode ?? CodeUtils.CreateLineCode(CodeUtils.EmtCodMode, line);

                arrives.Add(new ArriveDto
                {
                    Line = line,
                    LineCode = lineCode,
                    Destination = a.GetProperty("destination").GetString() ?? "",
                    CodMode = int.Parse(CodeUtils.EmtCodMode),
                    EstimatedArrive = estimatedArrive,
                });
            }
        }

        var incidents = new List<IncidentDto>();
        if (data.TryGetProperty("Incident", out var incidentEl)
            && incidentEl.TryGetProperty("ListaIncident", out var lista)
            && lista.TryGetProperty("data", out var incData)
            && incData.ValueKind == JsonValueKind.Array)
        {
            foreach (var inc in incData.EnumerateArray())
            {
                incidents.Add(new IncidentDto
                {
                    Title = inc.GetProperty("title").GetString() ?? "",
                    Description = inc.GetProperty("description").GetString() ?? "",
                    Cause = inc.GetProperty("cause").GetString() ?? "",
                    Effect = inc.GetProperty("effect").GetString() ?? "",
                    From = inc.GetProperty("rssFrom").GetString() ?? "",
                    To = inc.GetProperty("rssTo").GetString() ?? "",
                    Url = inc.TryGetProperty("moreInfo", out var mi) && mi.TryGetProperty("@url", out var urlEl)
                        ? urlEl.GetString() ?? "" : "",
                });
            }
        }

        return new StopTimesDto
        {
            CodMode = int.Parse(CodeUtils.EmtCodMode),
            StopName = stopName,
            SimpleStopCode = simpleStopCode,
            Coordinates = coordinates,
            Arrives = arrives != null ? GroupArrives(arrives) : null,
            Incidents = incidents,
        };
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
