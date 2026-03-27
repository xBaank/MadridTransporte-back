using MadridTransporte.Api.Data;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Exceptions;
using MadridTransporte.Api.Utils;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Services;

public class StopsService(AppDbContext db)
{
    public IAsyncEnumerable<StopDto> GetAllStopsAsync(CancellationToken ct = default)
    {
        return db
            .Stops.Select(s => new StopDto
            {
                StopCode = s.StopCode,
                StopName = s.StopName,
                StopLat = s.StopLat,
                StopLon = s.StopLon,
                CodMode = s.CodMode,
                FullStopCode = s.FullStopCode,
                Wheelchair = s.Wheelchair,
                Zone = s.Zone,
            })
            .AsAsyncEnumerable();
    }

    public async Task<StopDto> GetStopByFullStopCodeAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var stop =
            await db.Stops.FirstOrDefaultAsync(s => s.FullStopCode == fullStopCode, ct)
            ?? throw ApiException.NotFound($"Stop with id {fullStopCode} not found");

        return new StopDto
        {
            StopCode = stop.StopCode,
            StopName = stop.StopName,
            StopLat = stop.StopLat,
            StopLon = stop.StopLon,
            CodMode = stop.CodMode,
            FullStopCode = stop.FullStopCode,
            Wheelchair = stop.Wheelchair,
            Zone = stop.Zone,
        };
    }

    public async Task<string> GetStopNameByStopCodeAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var stop =
            await db.Stops.FirstOrDefaultAsync(s => s.FullStopCode == fullStopCode, ct)
            ?? throw ApiException.NotFound($"Stop with id {fullStopCode} not found");
        return stop.StopName;
    }

    public async Task<CoordinatesDto> GetCoordinatesByStopCodeAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var stop =
            await db.Stops.FirstOrDefaultAsync(s => s.FullStopCode == fullStopCode, ct)
            ?? throw ApiException.NotFound();
        return new CoordinatesDto { Latitude = stop.StopLat, Longitude = stop.StopLon };
    }

    public async Task<string?> GetIdByStopCodeAsync(string stopCode, CancellationToken ct = default)
    {
        var info = await db.StopInfos.FirstOrDefaultAsync(s => s.IdEstacion == stopCode, ct);
        return info?.CodigoEmpresa;
    }

    public async Task<string> GetStopNameByIdAsync(
        string codigoEmpresa,
        CancellationToken ct = default
    )
    {
        var info =
            await db.StopInfos.FirstOrDefaultAsync(s => s.CodigoEmpresa == codigoEmpresa, ct)
            ?? throw ApiException.NotFound();
        var stop =
            await db.Stops.FirstOrDefaultAsync(s => s.FullStopCode == info.IdEstacion, ct)
            ?? throw ApiException.NotFound();
        return stop.StopName;
    }

    public async Task<List<PlannedTimeDto>> GetStopTimesPlannedAsync(
        string fullStopCode,
        CancellationToken ct = default
    )
    {
        var madridNow = TimeUtils.GetMadridNow();
        var madridMidnight = new DateTimeOffset(
            madridNow.Date,
            TimeUtils.GetMadridTimeZone().GetUtcOffset(madridNow.DateTime)
        );
        var millisSinceStartOfDay = (long)(madridNow - madridMidnight).TotalMilliseconds;
        var nowEpochMs = madridNow.ToUnixTimeMilliseconds();

        var dayOfWeek = madridNow.DayOfWeek;
        var activeServiceIds = await db
            .Calendars.Where(c => c.StartDate <= nowEpochMs && c.EndDate >= nowEpochMs)
            .Where(c =>
                (dayOfWeek == DayOfWeek.Monday && c.Monday)
                || (dayOfWeek == DayOfWeek.Tuesday && c.Tuesday)
                || (dayOfWeek == DayOfWeek.Wednesday && c.Wednesday)
                || (dayOfWeek == DayOfWeek.Thursday && c.Thursday)
                || (dayOfWeek == DayOfWeek.Friday && c.Friday)
                || (dayOfWeek == DayOfWeek.Saturday && c.Saturday)
                || (dayOfWeek == DayOfWeek.Sunday && c.Sunday)
            )
            .Select(c => c.ServiceId)
            .ToListAsync(ct);

        var startOfDayMadridEpochMs = madridMidnight.ToUnixTimeMilliseconds();

        var stopOrders = await db
            .StopOrders.Where(so =>
                so.FullStopCode == fullStopCode && so.DepartureTime >= millisSinceStartOfDay
            )
            .Join(db.Itineraries, so => so.TripId, it => it.TripId, (so, it) => new { so, it })
            .Where(x => activeServiceIds.Contains(x.it.ServiceId))
            .ToListAsync(ct);

        var relevantLineCodes = stopOrders.Select(x => x.it.FullLineCode).Distinct().ToList();
        var routes = await db
            .Routes.Where(r => relevantLineCodes.Contains(r.FullLineCode))
            .ToListAsync(ct);
        var routeMap = routes.ToDictionary(r => r.FullLineCode);

        return stopOrders
            .GroupBy(x => new
            {
                x.it.FullLineCode,
                x.it.Direction,
                x.it.ItineraryCode,
            })
            .Select(g =>
            {
                routeMap.TryGetValue(g.Key.FullLineCode, out var route);
                var codModeStr =
                    route?.CodMode ?? CodeUtils.GetCodModeFromLineCode(g.Key.FullLineCode);
                return new PlannedTimeDto
                {
                    FullLineCode = g.Key.FullLineCode,
                    LineCode = route?.SimpleLineCode,
                    Destination = g.First().it.TripName,
                    CodMode = int.TryParse(codModeStr, out var cm) ? cm : null,
                    Direction = g.Key.Direction + 1,
                    ItineraryCode = g.Key.ItineraryCode,
                    Arrives = g.Select(x => x.so.DepartureTime + startOfDayMadridEpochMs)
                        .Distinct()
                        .OrderBy(t => t)
                        .ToList(),
                };
            })
            .ToList();
    }
}
