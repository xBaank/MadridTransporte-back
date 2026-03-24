using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Services;

public interface IItinerariesService
{
    Task<ItineraryDto?> GetItineraryByCodeAsync(string itineraryCode);
    Task<ItineraryDto?> GetItineraryByLineAndDirectionAsync(string fullLineCode, int direction, string? stopCode = null);
    Task<string?> GetFullLineCodeByItineraryCodeAsync(string itineraryCode);
}
