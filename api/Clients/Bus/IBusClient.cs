using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Clients.Bus;

public interface IBusClient
{
    Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode);
}
