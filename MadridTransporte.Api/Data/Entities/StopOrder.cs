namespace MadridTransporte.Api.Data.Entities;

public class StopOrder
{
    public required string FullStopCode { get; set; }
    public required string TripId { get; set; }
    public int Order { get; set; }
    public long DepartureTime { get; set; }
}
