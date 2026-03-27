using System.ServiceModel;
using System.Text;
using MadridTransporte.Api.Clients.Crtm.Generated;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Utils;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.ObjectPool;
using Gen = MadridTransporte.Api.Clients.Crtm.Generated;

namespace MadridTransporte.Api.Clients.Crtm;

public partial class CrtmClient(
    IConfiguration config,
    IMemoryCache cache,
    ILogger<CrtmClient> logger
)
{
    private readonly ObjectPool<MultimodalInformationClient> _pool =
        new DefaultObjectPool<MultimodalInformationClient>(new ClientPolicy(config));

    private static async Task<AuthHeader> GetAuthAsync(
        MultimodalInformationClient client,
        CancellationToken ct
    )
    {
        var response = await client.GetPublicKeyAsync().WaitAsync(ct);
        var connectionKey = CrtmAuthHelper.Encrypt(Encoding.UTF8.GetBytes(response.key));
        return new AuthHeader { connectionKey = connectionKey };
    }

    public async Task<StopTimesDto?> GetStopTimesAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var client = _pool.Get();
        try
        {
            var auth = await GetAuthAsync(client, ct);
            var response = await client
                .GetStopTimesAsync(auth, fullStopCode, 1, 2, null, null, null, 3)
                .WaitAsync(ct);
            return MapStopTimesResponse(response.stopTimes, fullStopCode);
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "CRTM GetStopTimes failed for {StopCode}", fullStopCode);
            return null;
        }
        finally
        {
            _pool.Return(client);
        }
    }

    public async Task<List<AlertDto>> GetAlertsAsync(string codMode, CancellationToken ct = default)
    {
        var cacheKey = $"crtm_alerts_{codMode}";
        if (cache.TryGetValue(cacheKey, out List<AlertDto>? cached) && cached is not null)
            return cached;

        var client = _pool.Get();
        try
        {
            var auth = await GetAuthAsync(client, ct);
            var response = await client
                .GetIncidentsAffectationsAsync(auth, codMode, [])
                .WaitAsync(ct);
            var alerts = MapAlertsResponse(response.incidentsAffectations);
            cache.Set(cacheKey, alerts, TimeSpan.FromHours(24));
            return alerts;
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Warning))
                logger.LogWarning(ex, "CRTM GetAlerts failed for codMode {CodMode}", codMode);
            if (cache.TryGetValue(cacheKey, out List<AlertDto>? fallback) && fallback is not null)
                return fallback;
            return [];
        }
        finally
        {
            _pool.Return(client);
        }
    }

    public async Task<ItineraryDto?> GetLineItinerariesAsync(
        string lineCode,
        int direction,
        CancellationToken ct = default
    )
    {
        var client = _pool.Get();
        try
        {
            var auth = await GetAuthAsync(client, ct);
            var response = await client.GetLineItinerariesAsync(auth, lineCode, 1).WaitAsync(ct);
            return MapItinerariesResponse(response.itineraries, lineCode, direction);
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Warning))
                logger.LogWarning(ex, "CRTM GetLineItineraries failed for {LineCode}", lineCode);
            return null;
        }
        finally
        {
            _pool.Return(client);
        }
    }

    public async Task<VehicleLocationsDto?> GetLineLocationsAsync(
        string lineCode,
        int direction,
        string codMode,
        string? stopCode,
        CancellationToken ct = default
    )
    {
        return await GetLocationsInternalAsync(lineCode, direction, null, codMode, stopCode, ct);
    }

    public async Task<VehicleLocationsDto?> GetLineLocationsByItineraryAsync(
        string lineCode,
        string itineraryCode,
        string codMode,
        string? stopCode,
        CancellationToken ct = default
    )
    {
        return await GetLocationsInternalAsync(lineCode, 0, itineraryCode, codMode, stopCode, ct);
    }

    private async Task<VehicleLocationsDto?> GetLocationsInternalAsync(
        string lineCode,
        int direction,
        string? itineraryCode,
        string codMode,
        string? stopCode,
        CancellationToken ct = default
    )
    {
        var client = _pool.Get();
        try
        {
            var auth = await GetAuthAsync(client, ct);
            var response = await client
                .GetLineLocationAsync(
                    auth,
                    codMode,
                    lineCode,
                    direction,
                    itineraryCode ?? "",
                    "",
                    stopCode ?? "8_"
                )
                .WaitAsync(ct);
            return MapLocationsResponse(response.vehiclesLocation);
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Warning))
                logger.LogWarning(ex, "CRTM GetLineLocation failed for {LineCode}", lineCode);
            return null;
        }
        finally
        {
            _pool.Return(client);
        }
    }

    private static StopTimesDto? MapStopTimesResponse(Gen.StopTime? stopTimes, string fullStopCode)
    {
        if (stopTimes is null)
            return null;

        var stop = stopTimes.stop;
        var stopName = stop?.name ?? "";
        var shortStopCode = stop?.shortCodStop ?? "";

        var arrives = (stopTimes.times ?? [])
            .Select(t => new ArriveDto
            {
                Line = t.line?.shortDescription ?? "",
                LineCode = t.line?.codLine,
                Direction = t.direction,
                CodMode = int.TryParse(t.line?.codMode, out var cm) ? cm : 8,
                Destination = t.destination ?? "",
                EstimatedArrive = new DateTimeOffset(
                    TimeZoneInfo.ConvertTimeToUtc(
                        DateTime.SpecifyKind(t.time, DateTimeKind.Unspecified),
                        TimeUtils.GetMadridTimeZone()
                    )
                ).ToUnixTimeMilliseconds(),
            })
            .ToList();

        return new StopTimesDto
        {
            CodMode = 8,
            StopName = stopName,
            SimpleStopCode = shortStopCode,
            Coordinates = new CoordinatesDto(),
            Arrives = ArriveDto.GroupArrives(arrives),
            Incidents = [],
        };
    }

    private static List<AlertDto> MapAlertsResponse(Gen.IncidentAffectation[]? incidents)
    {
        if (incidents is null)
            return [];

        return incidents
            .Select(ia => new AlertDto
            {
                Description = ia.description ?? "",
                CodMode = int.TryParse(ia.codMode, out var cm) ? cm : 0,
                CodLine = ia.codLine ?? "",
                Stops = (ia.stopsAffectated ?? []).Select(s => s.codStop ?? "").ToList(),
            })
            .ToList();
    }

    private static ItineraryDto? MapItinerariesResponse(
        Gen.LineItinerary[]? itineraries,
        string lineCode,
        int direction
    )
    {
        if (itineraries is null)
            return null;

        var matching = itineraries.FirstOrDefault(li => li.direction == direction);
        if (matching is null)
            return null;

        var stops = (matching.stops ?? [])
            .Select((s, i) => new StopOrderDto { FullStopCode = s.codStop ?? "", Order = i })
            .ToList();

        return new ItineraryDto
        {
            CodItinerary = matching.codItinerary ?? "",
            Direction = direction,
            Stops = stops,
        };
    }

    private static VehicleLocationsDto? MapLocationsResponse(Gen.VehicleLocation[]? vehicles)
    {
        var locations = (vehicles ?? [])
            .Select(vl => new VehicleLocationDto
            {
                LineCode = vl.line?.codLine ?? "",
                SimpleLineCode = vl.line?.shortDescription ?? "",
                CodVehicle = vl.codVehicle ?? "",
                Coordinates = new CoordinatesDto
                {
                    Latitude = vl.coordinates?.latitude ?? 0,
                    Longitude = vl.coordinates?.longitude ?? 0,
                },
                Direction = vl.direction,
                Service = vl.service ?? "",
            })
            .ToList();

        return new VehicleLocationsDto
        {
            CodMode = 0,
            LineCode = "",
            Locations = locations,
        };
    }
}
