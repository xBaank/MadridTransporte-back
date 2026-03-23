using MadridTransporte.Api.Data;
using MadridTransporte.Api.Dtos;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Services;

public class ItinerariesService(AppDbContext db) : IItinerariesService
{
    public async Task<ItineraryDto?> GetItineraryByCodeAsync(string itineraryCode)
    {
        var itinerary = await db.Itineraries
            .FirstOrDefaultAsync(i => i.ItineraryCode == itineraryCode);

        if (itinerary == null) return null;

        var stops = await db.StopOrders
            .Where(so => so.TripId == itinerary.TripId)
            .Select(so => new StopOrderDto
            {
                FullStopCode = so.FullStopCode,
                Order = so.Order,
            })
            .Distinct()
            .OrderBy(so => so.Order)
            .ToListAsync();

        return new ItineraryDto
        {
            CodItinerary = itinerary.ItineraryCode,
            Direction = itinerary.Direction + 1,
            Stops = stops,
        };
    }

    public async Task<ItineraryDto?> GetItineraryByLineAndDirectionAsync(string fullLineCode, int direction, string? stopCode = null)
    {
        var itinerary = await db.Itineraries
            .FirstOrDefaultAsync(i => i.FullLineCode == fullLineCode && i.Direction == direction - 1);

        if (itinerary == null) return null;

        var stops = await db.StopOrders
            .Where(so => so.TripId == itinerary.TripId)
            .Select(so => new StopOrderDto
            {
                FullStopCode = so.FullStopCode,
                Order = so.Order,
            })
            .Distinct()
            .OrderBy(so => so.Order)
            .ToListAsync();

        if (stopCode != null && stops.All(s => s.FullStopCode != stopCode))
            return null;

        return new ItineraryDto
        {
            CodItinerary = itinerary.ItineraryCode,
            Direction = itinerary.Direction + 1,
            Stops = stops,
        };
    }

    public async Task<string?> GetFullLineCodeByItineraryCodeAsync(string itineraryCode)
    {
        var itinerary = await db.Itineraries
            .FirstOrDefaultAsync(i => i.ItineraryCode == itineraryCode);
        return itinerary?.FullLineCode;
    }
}
