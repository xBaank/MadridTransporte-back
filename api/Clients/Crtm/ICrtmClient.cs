using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Clients.Crtm;

public interface ICrtmClient
{
    Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode);
    Task<List<AlertDto>> GetAlertsAsync(string codMode);
    Task<ItineraryDto?> GetLineItinerariesAsync(string lineCode, int direction);
    Task<VehicleLocationsDto?> GetLineLocationsAsync(string lineCode, int direction, string codMode, string? stopCode);
    Task<VehicleLocationsDto?> GetLineLocationsByItineraryAsync(string lineCode, string itineraryCode, string codMode, string? stopCode);
}
