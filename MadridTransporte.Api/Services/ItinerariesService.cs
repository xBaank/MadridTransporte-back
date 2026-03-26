using MadridTransporte.Api.Data;
using MadridTransporte.Api.Dtos;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Services;

public class ItinerariesService(AppDbContext db)
{
    public async Task<ItineraryDto?> GetItineraryByCodeAsync(
        string itineraryCode,
        CancellationToken ct = default
    )
    {
        var itinerary = await db.Itineraries.FirstOrDefaultAsync(
            i => i.ItineraryCode == itineraryCode,
            ct
        );

        if (itinerary is null)
            return null;

        var stops = await db
            .StopOrders.Where(so => so.TripId == itinerary.TripId)
            .Select(so => new StopOrderDto { FullStopCode = so.FullStopCode, Order = so.Order })
            .Distinct()
            .OrderBy(so => so.Order)
            .ToListAsync(ct);

        return new ItineraryDto
        {
            CodItinerary = itinerary.ItineraryCode,
            Direction = itinerary.Direction + 1,
            Stops = stops,
        };
    }

    public async Task<ItineraryDto?> GetItineraryByLineAndDirectionAsync(
        string fullLineCode,
        int direction,
        string? stopCode = null,
        CancellationToken ct = default
    )
    {
        var query = db.Itineraries.Where(i =>
            i.FullLineCode == fullLineCode && i.Direction == direction - 1
        );

        if (stopCode is not null)
        {
            var tripIdsWithStop = db
                .StopOrders.Where(so => so.FullStopCode == stopCode)
                .Select(so => so.TripId);
            query = query.Where(i => tripIdsWithStop.Contains(i.TripId));
        }

        var itinerary = await query.FirstOrDefaultAsync(ct);
        if (itinerary is null && stopCode is not null)
        {
            // stopCode filter found no matching trips; fall back to any itinerary for this line/direction
            itinerary = await db
                .Itineraries.Where(i =>
                    i.FullLineCode == fullLineCode && i.Direction == direction - 1
                )
                .FirstOrDefaultAsync(ct);
        }

        if (itinerary is null)
            return null;

        var stops = await db
            .StopOrders.Where(so => so.TripId == itinerary.TripId)
            .Select(so => new StopOrderDto { FullStopCode = so.FullStopCode, Order = so.Order })
            .Distinct()
            .OrderBy(so => so.Order)
            .ToListAsync(ct);

        return new ItineraryDto
        {
            CodItinerary = itinerary.ItineraryCode,
            Direction = itinerary.Direction + 1,
            Stops = stops,
        };
    }

    public async Task<string?> GetFullLineCodeByItineraryCodeAsync(
        string itineraryCode,
        CancellationToken ct = default
    )
    {
        var itinerary = await db.Itineraries.FirstOrDefaultAsync(
            i => i.ItineraryCode == itineraryCode,
            ct
        );
        return itinerary?.FullLineCode;
    }
}
