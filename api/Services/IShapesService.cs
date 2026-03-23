using MadridTransporte.Api.Dtos;

namespace MadridTransporte.Api.Services;

public interface IShapesService
{
    Task<List<ShapeDto>> GetShapesByItineraryCodeAsync(string itineraryCode);
}
