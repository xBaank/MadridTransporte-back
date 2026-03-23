namespace MadridTransporte.Api.Dtos;

public class ItineraryDto
{
    public required string CodItinerary { get; set; }
    public int Direction { get; set; }
    public required List<StopOrderDto> Stops { get; set; }
}

public class StopOrderDto
{
    public required string FullStopCode { get; set; }
    public int Order { get; set; }
}
