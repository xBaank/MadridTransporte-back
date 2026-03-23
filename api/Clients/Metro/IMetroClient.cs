using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Clients.Metro;

public interface IMetroClient
{
    Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode);
}
