namespace MadridTransporte.Api.Dtos;

public class StopDto
{
    public required string StopCode { get; set; }
    public required string StopName { get; set; }
    public double StopLat { get; set; }
    public double StopLon { get; set; }
    public int CodMode { get; set; }
    public required string FullStopCode { get; set; }
    public int Wheelchair { get; set; }
    public required string Zone { get; set; }
}
