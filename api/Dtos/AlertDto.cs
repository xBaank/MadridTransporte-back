namespace MadridTransporte.Api.Dtos;

public class AlertDto
{
    public required string Description { get; set; }
    public int CodMode { get; set; }
    public required string CodLine { get; set; }
    public required List<string> Stops { get; set; }
}
