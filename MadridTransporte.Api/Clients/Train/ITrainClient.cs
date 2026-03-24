using System.Text.Json;
using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Clients.Train;

public interface ITrainClient
{
    Task<StopTimesDto?> GetStopTimesAsync(string fullStopCode);
    Task<JsonElement?> GetRoutedTimesAsync(string originStopCode, string destinationStopCode);
}
