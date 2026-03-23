using MadridTransporte.Api.Data;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Api.Dtos;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Services;

public class RoutesService(AppDbContext db) : IRoutesService
{
    public async Task<TransitRoute?> GetRouteByFullLineCodeAsync(string fullLineCode)
    {
        return await db.Routes.FirstOrDefaultAsync(r => r.FullLineCode == fullLineCode);
    }

    public async Task<TransitRoute?> GetRouteAsync(string simpleLineCode, string codMode)
    {
        return await db.Routes.FirstOrDefaultAsync(r =>
            r.SimpleLineCode == simpleLineCode && r.CodMode == codMode);
    }

    public async Task<TransitRoute?> GetRouteAsync(string simpleLineCode, List<string> codModes)
    {
        return await db.Routes.FirstOrDefaultAsync(r =>
            r.SimpleLineCode == simpleLineCode && codModes.Contains(r.CodMode));
    }

    public async Task<List<RouteDto>> GetRoutesWithItinerariesAsync()
    {
        var routes = await db.Routes.ToListAsync();
        var itineraries = await db.Itineraries.ToListAsync();
        var itineraryLookup = itineraries.ToLookup(i => i.FullLineCode);

        return routes.Select(r => new RouteDto
        {
            FullLineCode = r.FullLineCode,
            SimpleLineCode = r.SimpleLineCode,
            CodMode = int.TryParse(r.CodMode, out var cm) ? cm : 0,
            RouteName = r.RouteName,
            Itineraries = itineraryLookup[r.FullLineCode]
                .DistinctBy(i => i.ItineraryCode)
                .Select(i => new RouteItineraryDto
                {
                    ItineraryCode = i.ItineraryCode,
                    Direction = i.Direction + 1,
                    TripName = i.TripName,
                    ServiceId = i.ServiceId,
                })
                .ToList(),
        }).ToList();
    }
}
