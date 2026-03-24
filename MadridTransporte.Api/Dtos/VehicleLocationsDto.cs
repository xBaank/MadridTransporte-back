namespace MadridTransporte.Api.Dtos;

public class VehicleLocationsDto
{
    public int CodMode { get; set; }
    public required string LineCode { get; set; }
    public required List<VehicleLocationDto> Locations { get; set; }
}

public class VehicleLocationDto
{
    public required string LineCode { get; set; }
    public required string SimpleLineCode { get; set; }
    public required string CodVehicle { get; set; }
    public required CoordinatesDto Coordinates { get; set; }
    public int Direction { get; set; }
    public required string Service { get; set; }
}
