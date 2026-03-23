using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Services;

public interface IStopsService
{
    Task<List<StopDto>> GetAllStopsAsync();
    Task<StopDto> GetStopByFullStopCodeAsync(string fullStopCode);
    Task<string> GetStopNameByStopCodeAsync(string fullStopCode);
    Task<CoordinatesDto> GetCoordinatesByStopCodeAsync(string fullStopCode);
    Task<string?> GetIdByStopCodeAsync(string stopCode);
    Task<string> GetStopNameByIdAsync(string codigoEmpresa);
    Task<List<PlannedTimeDto>> GetStopTimesPlannedAsync(string fullStopCode);
}
