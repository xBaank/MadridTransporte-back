using System.Runtime.CompilerServices;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Api.Dtos;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Services;

public class RoutesService(AppDbContext db)
{
    public async Task<TransitRoute?> GetRouteByFullLineCodeAsync(
        string fullLineCode,
        CancellationToken ct = default
    )
    {
        return await db.Routes.FirstOrDefaultAsync(r => r.FullLineCode == fullLineCode, ct);
    }

    public async Task<TransitRoute?> GetRouteAsync(
        string simpleLineCode,
        string codMode,
        CancellationToken ct = default
    )
    {
        return await db.Routes.FirstOrDefaultAsync(
            r => r.SimpleLineCode == simpleLineCode && r.CodMode == codMode,
            ct
        );
    }

    public async Task<TransitRoute?> GetRouteAsync(
        string simpleLineCode,
        List<string> codModes,
        CancellationToken ct = default
    )
    {
        return await db.Routes.FirstOrDefaultAsync(
            r => r.SimpleLineCode == simpleLineCode && codModes.Contains(r.CodMode),
            ct
        );
    }

    public async IAsyncEnumerable<RouteDto> GetRoutesWithItinerariesAsync(
        [EnumeratorCancellation] CancellationToken ct = default
    )
    {
        var itineraries = await db.Itineraries.ToListAsync(ct);
        var itineraryLookup = itineraries.ToLookup(i => i.FullLineCode);

        await foreach (var r in db.Routes.AsAsyncEnumerable().WithCancellation(ct))
        {
            yield return new RouteDto
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
            };
        }
    }
}
