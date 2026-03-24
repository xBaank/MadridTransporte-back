using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Clients.Emt;

public interface IEmtClient
{
    Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode);
}
