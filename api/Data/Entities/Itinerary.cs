namespace MadridTransporte.Api.Data.Entities;

public class Itinerary
{
    public int Id { get; set; }
    public required string ItineraryCode { get; set; }
    public required string FullLineCode { get; set; }
    public int Direction { get; set; }
    public required string TripId { get; set; }
    public required string ServiceId { get; set; }
    public required string TripName { get; set; }
}
