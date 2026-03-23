using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Services;

public interface IRoutesService
{
    Task<TransitRoute?> GetRouteByFullLineCodeAsync(string fullLineCode);
    Task<TransitRoute?> GetRouteAsync(string simpleLineCode, string codMode);
    Task<TransitRoute?> GetRouteAsync(string simpleLineCode, List<string> codModes);
    Task<List<RouteDto>> GetRoutesWithItinerariesAsync();
}
