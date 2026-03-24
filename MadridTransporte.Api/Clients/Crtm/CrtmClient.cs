using System.Text;
using MadridTransporte.Api.Clients.Crtm.Generated;
using MadridTransporte.Api.Dtos;
using Microsoft.Extensions.Caching.Memory;
using Gen = MadridTransporte.Api.Clients.Crtm.Generated;

namespace MadridTransporte.Api.Clients.Crtm;

public class CrtmClient(IConfiguration config, IMemoryCache cache, ILogger<CrtmClient> logger) : ICrtmClient
{
    private string Endpoint => config["Crtm:Endpoint"] ?? "http://www.citram.es:8080/WSMultimodalInformation/MultimodalInformation.svc";

    private MultimodalInformationClient CreateSoapClient()
    {
        var binding = new System.ServiceModel.BasicHttpBinding
        {
            MaxBufferSize = int.MaxValue,
            MaxReceivedMessageSize = int.MaxValue,
            ReaderQuotas = System.Xml.XmlDictionaryReaderQuotas.Max,
            AllowCookies = true,
            SendTimeout = TimeSpan.FromSeconds(config.GetValue("Crtm:TimeoutSeconds", 30)),
        };
        var endpoint = new System.ServiceModel.EndpointAddress(Endpoint);
        return new MultimodalInformationClient(binding, endpoint);
    }

    private async Task<AuthHeader> GetAuthAsync()
    {
        var client = CreateSoapClient();
        try
        {
            var response = await client.GetPublicKeyAsync();
            var publicKey = response.key;
            var connectionKey = CrtmAuthHelper.Encrypt(Encoding.UTF8.GetBytes(publicKey));
            return new AuthHeader { connectionKey = connectionKey };
        }
        finally
        {
            await client.CloseAsync();
        }
    }

    public async Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode)
    {
        try
        {
            var auth = await GetAuthAsync();
            var client = CreateSoapClient();
            try
            {
                var response = await client.GetStopTimesAsync(auth, fullStopCode, 1, 2, null, null, null, 3);
                return MapStopTimesResponse(response.stopTimes, fullStopCode);
            }
            finally
            {
                await client.CloseAsync();
            }
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "CRTM GetStopTimes failed for {StopCode}", fullStopCode);
            return null;
        }
    }

    public async Task<List<AlertDto>> GetAlertsAsync(string codMode)
    {
        var cacheKey = $"crtm_alerts_{codMode}";
        if (cache.TryGetValue(cacheKey, out List<AlertDto>? cached) && cached != null)
            return cached;

        try
        {
            var auth = await GetAuthAsync();
            var client = CreateSoapClient();
            try
            {
                var response = await client.GetIncidentsAffectationsAsync(auth, codMode, []);
                var alerts = MapAlertsResponse(response.incidentsAffectations);
                cache.Set(cacheKey, alerts, TimeSpan.FromHours(24));
                return alerts;
            }
            finally
            {
                await client.CloseAsync();
            }
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "CRTM GetAlerts failed for codMode {CodMode}", codMode);
            if (cache.TryGetValue(cacheKey, out List<AlertDto>? fallback) && fallback != null)
                return fallback;
            return [];
        }
    }

    public async Task<ItineraryDto?> GetLineItinerariesAsync(string lineCode, int direction)
    {
        try
        {
            var auth = await GetAuthAsync();
            var client = CreateSoapClient();
            try
            {
                var response = await client.GetLineItinerariesAsync(auth, lineCode, 1);
                return MapItinerariesResponse(response.itineraries, lineCode, direction);
            }
            finally
            {
                await client.CloseAsync();
            }
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "CRTM GetLineItineraries failed for {LineCode}", lineCode);
            return null;
        }
    }

    public async Task<VehicleLocationsDto?> GetLineLocationsAsync(string lineCode, int direction, string codMode, string? stopCode)
    {
        return await GetLocationsInternalAsync(lineCode, direction, null, codMode, stopCode);
    }

    public async Task<VehicleLocationsDto?> GetLineLocationsByItineraryAsync(string lineCode, string itineraryCode, string codMode, string? stopCode)
    {
        return await GetLocationsInternalAsync(lineCode, 0, itineraryCode, codMode, stopCode);
    }

    private async Task<VehicleLocationsDto?> GetLocationsInternalAsync(string lineCode, int direction, string? itineraryCode, string codMode, string? stopCode)
    {
        try
        {
            var auth = await GetAuthAsync();
            var client = CreateSoapClient();
            try
            {
                var response = await client.GetLineLocationAsync(
                    auth, codMode, lineCode, direction,
                    itineraryCode ?? "", "", stopCode ?? "8_");
                return MapLocationsResponse(response.vehiclesLocation);
            }
            finally
            {
                await client.CloseAsync();
            }
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "CRTM GetLineLocation failed for {LineCode}", lineCode);
            return null;
        }
    }

    private static StopTimesDto? MapStopTimesResponse(Gen.StopTime? stopTimes, string fullStopCode)
    {
        if (stopTimes == null) return null;

        var stop = stopTimes.stop;
        var stopName = stop?.name ?? "";
        var shortStopCode = stop?.shortCodStop ?? "";

        var arrives = (stopTimes.times ?? []).Select(t => new ArriveDto
        {
            Line = t.line?.shortDescription ?? "",
            LineCode = t.line?.codLine,
            Direction = t.direction,
            CodMode = int.TryParse(t.line?.codMode, out var cm) ? cm : 8,
            Destination = t.destination ?? "",
            EstimatedArrive = new DateTimeOffset(DateTime.SpecifyKind(t.time, DateTimeKind.Utc)).ToUnixTimeMilliseconds(),
        }).ToList();

        return new StopTimesDto
        {
            CodMode = 8,
            StopName = stopName,
            SimpleStopCode = shortStopCode,
            Coordinates = new CoordinatesDto(),
            Arrives = arrives.Count > 0 ? GroupArrives(arrives) : null,
            Incidents = [],
        };
    }

    private static List<ArriveGroupDto> GroupArrives(List<ArriveDto> arrives)
    {
        return arrives
            .OrderBy(a => int.TryParse(a.Line, out var n) ? n : int.MaxValue)
            .GroupBy(a => (a.Line, a.Destination, a.Anden))
            .Where(g => g.Any())
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

    private static List<AlertDto> MapAlertsResponse(Gen.IncidentAffectation[]? incidents)
    {
        if (incidents == null) return [];

        return incidents.Select(ia => new AlertDto
        {
            Description = ia.description ?? "",
            CodMode = int.TryParse(ia.codMode, out var cm) ? cm : 0,
            CodLine = ia.codLine ?? "",
            Stops = (ia.stopsAffectated ?? [])
                .Select(s => s.codStop ?? "")
                .ToList(),
        }).ToList();
    }

    private static ItineraryDto? MapItinerariesResponse(Gen.LineItinerary[]? itineraries, string lineCode, int direction)
    {
        if (itineraries == null) return null;

        var matching = itineraries.FirstOrDefault(li => li.direction == direction);
        if (matching == null) return null;

        var stops = (matching.stops ?? [])
            .Select((s, i) => new StopOrderDto
            {
                FullStopCode = s.codStop ?? "",
                Order = i,
            })
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
        var locations = (vehicles ?? []).Select(vl => new VehicleLocationDto
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
        }).ToList();

        return new VehicleLocationsDto
        {
            CodMode = 0,
            LineCode = "",
            Locations = locations,
        };
    }
}
