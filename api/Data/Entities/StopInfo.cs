namespace MadridTransporte.Api.Data.Entities;

public class StopInfo
{
    public int Id { get; set; }
    public required string CodigoEmpresa { get; set; }
    public required string IdEstacion { get; set; }
}
