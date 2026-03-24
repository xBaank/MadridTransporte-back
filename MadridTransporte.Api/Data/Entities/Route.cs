namespace MadridTransporte.Api.Data.Entities;

public class TransitRoute
{
    public required string FullLineCode { get; set; }
    public required string SimpleLineCode { get; set; }
    public required string RouteName { get; set; }
    public required string CodMode { get; set; }
}
