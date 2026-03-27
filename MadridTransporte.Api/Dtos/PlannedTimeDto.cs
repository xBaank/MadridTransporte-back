namespace MadridTransporte.Api.Dtos;

public class PlannedTimeDto
{
    public required string FullLineCode { get; set; }
    public string? LineCode { get; set; }
    public required string Destination { get; set; }
    public int? CodMode { get; set; }
    public int Direction { get; set; }
    public required string ItineraryCode { get; set; }
    public required List<long> Arrives { get; set; }
}
