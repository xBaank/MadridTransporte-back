using System.Text;
using System.Xml.Linq;
using MadridTransporte.Api.Dtos;
using Microsoft.Extensions.Caching.Memory;

namespace MadridTransporte.Api.Clients.Crtm;

public class CrtmClient(HttpClient httpClient, IConfiguration config, IMemoryCache cache, ILogger<CrtmClient> logger) : ICrtmClient
{
    private static readonly XNamespace Soapenv = "http://schemas.xmlsoap.org/soap/envelope/";
    private static readonly XNamespace Ns = "http://tempuri.org/";

    private string Endpoint => config["Crtm:Endpoint"] ?? "http://www.citram.es:8080/WSMultimodalInformation/MultimodalInformation.svc";
    private int TimeoutSeconds => config.GetValue("Crtm:TimeoutSeconds", 30);

    private async Task<string> GetAuthConnectionKeyAsync()
    {
        var publicKeyEnvelope = new XElement(Soapenv + "Envelope",
            new XAttribute(XNamespace.Xmlns + "soapenv", Soapenv.NamespaceName),
            new XAttribute(XNamespace.Xmlns + "tem", Ns.NamespaceName),
            new XElement(Soapenv + "Header"),
            new XElement(Soapenv + "Body",
                new XElement(Ns + "GetPublicKey",
                    new XElement(Ns + "request"))));

        var response = await SendSoapRequestAsync(publicKeyEnvelope, "GetPublicKey");
        var keyElement = response.Descendants().FirstOrDefault(e => e.Name.LocalName == "Key");
        var publicKey = keyElement?.Value ?? throw new InvalidOperationException("Failed to get CRTM public key");
        return CrtmAuthHelper.Encrypt(Encoding.UTF8.GetBytes(publicKey));
    }

    private XElement CreateAuthElement(string connectionKey) =>
        new(Ns + "Authentication",
            new XElement(Ns + "ConnectionKey", connectionKey));

    private async Task<XElement> SendSoapRequestAsync(XElement envelope, string action)
    {
        using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(TimeoutSeconds));
        var content = new StringContent(envelope.ToString(), Encoding.UTF8, "text/xml");
        content.Headers.Add("SOAPAction", $"http://tempuri.org/IMultimodalInformation/{action}");

        var response = await httpClient.PostAsync(Endpoint, content, cts.Token);
        response.EnsureSuccessStatusCode();
        var xml = await response.Content.ReadAsStringAsync(cts.Token);
        return XElement.Parse(xml);
    }

    public async Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode)
    {
        try
        {
            var connectionKey = await GetAuthConnectionKeyAsync();
            var envelope = new XElement(Soapenv + "Envelope",
                new XAttribute(XNamespace.Xmlns + "soapenv", Soapenv.NamespaceName),
                new XAttribute(XNamespace.Xmlns + "tem", Ns.NamespaceName),
                new XElement(Soapenv + "Header"),
                new XElement(Soapenv + "Body",
                    new XElement(Ns + "GetStopTimes",
                        new XElement(Ns + "request",
                            new XElement(Ns + "CodStop", fullStopCode),
                            new XElement(Ns + "Type", 1),
                            new XElement(Ns + "OrderBy", 2),
                            new XElement(Ns + "StopTimesByIti", 3),
                            CreateAuthElement(connectionKey)))));

            var result = await SendSoapRequestAsync(envelope, "GetStopTimes");
            return ParseStopTimesResponse(result, fullStopCode);
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
            var connectionKey = await GetAuthConnectionKeyAsync();
            var envelope = new XElement(Soapenv + "Envelope",
                new XAttribute(XNamespace.Xmlns + "soapenv", Soapenv.NamespaceName),
                new XAttribute(XNamespace.Xmlns + "tem", Ns.NamespaceName),
                new XElement(Soapenv + "Header"),
                new XElement(Soapenv + "Body",
                    new XElement(Ns + "GetIncidentsAffectations",
                        new XElement(Ns + "request",
                            new XElement(Ns + "CodMode", codMode),
                            new XElement(Ns + "CodLines"),
                            CreateAuthElement(connectionKey)))));

            var result = await SendSoapRequestAsync(envelope, "GetIncidentsAffectations");
            var alerts = ParseAlertsResponse(result);
            cache.Set(cacheKey, alerts, TimeSpan.FromHours(24));
            return alerts;
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
            var connectionKey = await GetAuthConnectionKeyAsync();
            var envelope = new XElement(Soapenv + "Envelope",
                new XAttribute(XNamespace.Xmlns + "soapenv", Soapenv.NamespaceName),
                new XAttribute(XNamespace.Xmlns + "tem", Ns.NamespaceName),
                new XElement(Soapenv + "Header"),
                new XElement(Soapenv + "Body",
                    new XElement(Ns + "GetLineItineraries",
                        new XElement(Ns + "request",
                            new XElement(Ns + "CodLine", lineCode),
                            new XElement(Ns + "Active", 1),
                            CreateAuthElement(connectionKey)))));

            var result = await SendSoapRequestAsync(envelope, "GetLineItineraries");
            return ParseItinerariesResponse(result, lineCode, direction);
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
            var connectionKey = await GetAuthConnectionKeyAsync();
            var requestElements = new List<XElement>
            {
                new(Ns + "CodMode", codMode),
                new(Ns + "CodLine", lineCode),
                new(Ns + "CodStop", stopCode ?? "8_"),
                CreateAuthElement(connectionKey)
            };

            if (itineraryCode != null)
                requestElements.Insert(2, new XElement(Ns + "CodItinerary", itineraryCode));
            else
                requestElements.Insert(2, new XElement(Ns + "Direction", direction));

            var envelope = new XElement(Soapenv + "Envelope",
                new XAttribute(XNamespace.Xmlns + "soapenv", Soapenv.NamespaceName),
                new XAttribute(XNamespace.Xmlns + "tem", Ns.NamespaceName),
                new XElement(Soapenv + "Header"),
                new XElement(Soapenv + "Body",
                    new XElement(Ns + "GetLineLocation",
                        new XElement(Ns + "request", requestElements))));

            var result = await SendSoapRequestAsync(envelope, "GetLineLocation");
            return ParseLocationsResponse(result);
        }
        catch (Exception ex)
        {
            logger.LogWarning(ex, "CRTM GetLineLocation failed for {LineCode}", lineCode);
            return null;
        }
    }

    private static StopTimesDto? ParseStopTimesResponse(XElement xml, string fullStopCode)
    {
        var times = xml.Descendants().Where(e => e.Name.LocalName == "Time").ToList();
        var stopEl = xml.Descendants().FirstOrDefault(e => e.Name.LocalName == "Stop");
        var stopName = stopEl?.Elements().FirstOrDefault(e => e.Name.LocalName == "Name")?.Value ?? "";
        var shortStopCode = stopEl?.Elements().FirstOrDefault(e => e.Name.LocalName == "ShortCodStop")?.Value ?? "";

        var arrives = times.Select(t => new ArriveDto
        {
            Line = t.Descendants().FirstOrDefault(e => e.Name.LocalName == "ShortDescription")?.Value ?? "",
            LineCode = t.Descendants().FirstOrDefault(e => e.Name.LocalName == "CodLine")?.Value,
            Direction = int.TryParse(t.Elements().FirstOrDefault(e => e.Name.LocalName == "Direction")?.Value, out var d) ? d : null,
            CodMode = int.TryParse(t.Descendants().FirstOrDefault(e => e.Name.LocalName == "CodMode")?.Value, out var cm) ? cm : 8,
            Destination = t.Elements().FirstOrDefault(e => e.Name.LocalName == "Destination")?.Value ?? "",
            EstimatedArrive = ParseSoapTime(t.Elements().FirstOrDefault(e => e.Name.LocalName == "Time")?.Value),
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

    private static long ParseSoapTime(string? timeStr)
    {
        if (timeStr == null) return 0;
        if (DateTimeOffset.TryParse(timeStr, out var dto))
            return dto.ToUnixTimeMilliseconds();
        return 0;
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

    private static List<AlertDto> ParseAlertsResponse(XElement xml)
    {
        return xml.Descendants()
            .Where(e => e.Name.LocalName == "IncidentAffectation")
            .Select(ia => new AlertDto
            {
                Description = ia.Elements().FirstOrDefault(e => e.Name.LocalName == "Description")?.Value ?? "",
                CodMode = int.TryParse(ia.Elements().FirstOrDefault(e => e.Name.LocalName == "CodMode")?.Value, out var cm) ? cm : 0,
                CodLine = ia.Elements().FirstOrDefault(e => e.Name.LocalName == "CodLine")?.Value ?? "",
                Stops = ia.Descendants()
                    .Where(e => e.Name.LocalName == "ShortStop")
                    .Select(s => s.Elements().FirstOrDefault(e => e.Name.LocalName == "CodStop")?.Value ?? "")
                    .ToList(),
            })
            .ToList();
    }

    private static ItineraryDto? ParseItinerariesResponse(XElement xml, string lineCode, int direction)
    {
        var itineraries = xml.Descendants().Where(e => e.Name.LocalName == "LineItinerary").ToList();
        var matching = itineraries.FirstOrDefault(li =>
        {
            var dir = li.Elements().FirstOrDefault(e => e.Name.LocalName == "Direction")?.Value;
            return dir != null && int.TryParse(dir, out var d) && d == direction;
        });

        if (matching == null) return null;

        var codItinerary = matching.Elements().FirstOrDefault(e => e.Name.LocalName == "CodItinerary")?.Value ?? "";
        var stops = matching.Descendants()
            .Where(e => e.Name.LocalName == "ShortStop")
            .Select((s, i) => new StopOrderDto
            {
                FullStopCode = s.Elements().FirstOrDefault(e => e.Name.LocalName == "CodStop")?.Value ?? "",
                Order = i,
            })
            .ToList();

        return new ItineraryDto
        {
            CodItinerary = codItinerary,
            Direction = direction,
            Stops = stops,
        };
    }

    private static VehicleLocationsDto? ParseLocationsResponse(XElement xml)
    {
        var locations = xml.Descendants()
            .Where(e => e.Name.LocalName == "VehicleLocation")
            .Select(vl => new VehicleLocationDto
            {
                LineCode = vl.Descendants().FirstOrDefault(e => e.Name.LocalName == "CodLine")?.Value ?? "",
                SimpleLineCode = vl.Descendants().FirstOrDefault(e => e.Name.LocalName == "ShortDescription")?.Value ?? "",
                CodVehicle = vl.Elements().FirstOrDefault(e => e.Name.LocalName == "CodVehicle")?.Value ?? "",
                Coordinates = new CoordinatesDto
                {
                    Latitude = double.TryParse(vl.Descendants().FirstOrDefault(e => e.Name.LocalName == "Latitude")?.Value, System.Globalization.CultureInfo.InvariantCulture, out var lat) ? lat : 0,
                    Longitude = double.TryParse(vl.Descendants().FirstOrDefault(e => e.Name.LocalName == "Longitude")?.Value, System.Globalization.CultureInfo.InvariantCulture, out var lon) ? lon : 0,
                },
                Direction = int.TryParse(vl.Elements().FirstOrDefault(e => e.Name.LocalName == "Direction")?.Value, out var d) ? d : 0,
                Service = vl.Elements().FirstOrDefault(e => e.Name.LocalName == "Service")?.Value ?? "",
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
