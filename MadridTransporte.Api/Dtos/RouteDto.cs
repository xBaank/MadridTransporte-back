namespace MadridTransporte.Api.Dtos;

public class RouteDto
{
    public required string FullLineCode { get; set; }
    public required string SimpleLineCode { get; set; }
    public int CodMode { get; set; }
    public required string RouteName { get; set; }
    public required List<RouteItineraryDto> Itineraries { get; set; }
}

public class RouteItineraryDto
{
    public required string ItineraryCode { get; set; }
    public int Direction { get; set; }
    public required string TripName { get; set; }
    public required string ServiceId { get; set; }
}
