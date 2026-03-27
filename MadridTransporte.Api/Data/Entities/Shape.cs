namespace MadridTransporte.Api.Data.Entities;

public class Shape
{
    public required string ItineraryId { get; set; }
    public double Latitude { get; set; }
    public double Longitude { get; set; }
    public int Sequence { get; set; }
    public double Distance { get; set; }
}
