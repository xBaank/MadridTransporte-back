using MadridTransporte.Api.Data;
using MadridTransporte.Api.Dtos;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Services;

public class ShapesService(AppDbContext db)
{
    public async Task<List<ShapeDto>> GetShapesByItineraryCodeAsync(
        string itineraryCode,
        CancellationToken ct = default
    )
    {
        return await db
            .Shapes.Where(s => s.ItineraryId == itineraryCode)
            .OrderBy(s => s.Sequence)
            .Select(s => new ShapeDto
            {
                Latitude = s.Latitude,
                Longitude = s.Longitude,
                Sequence = s.Sequence,
                Distance = s.Distance,
            })
            .ToListAsync(ct);
    }
}
