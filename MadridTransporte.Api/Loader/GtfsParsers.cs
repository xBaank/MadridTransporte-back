using System.Globalization;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Api.Utils;
using Microsoft.Extensions.Logging;

namespace MadridTransporte.Api.Loader;

public static class GtfsParsers
{
    public static Stop? ParseStop(Dictionary<string, string> data, int feedCodMode, ILogger logger)
    {
        try
        {
            var stopId = data.GetValueOrDefault("stop_id", "");
            int codMode;
            string stopCode;

            if (stopId.StartsWith("par_"))
            {
                // Format: par_8_09568 → codMode=8, stopCode=09568
                var parts = stopId.Split('_');
                codMode = int.TryParse(parts[1], out var cm) ? cm : feedCodMode;
                stopCode = data.GetValueOrDefault("stop_code", "").Trim();
            }
            else
            {
                // EMT-style numeric stop_id: 73 → codMode from feed, stopCode=73
                codMode = feedCodMode;
                stopCode = stopId.Trim();
            }

            return new Stop
            {
                StopCode = stopCode,
                StopName = data.GetValueOrDefault("stop_name", ""),
                StopLat = double.TryParse(
                    data.GetValueOrDefault("stop_lat", "0"),
                    CultureInfo.InvariantCulture,
                    out var lat
                )
                    ? lat
                    : 0.0,
                StopLon = double.TryParse(
                    data.GetValueOrDefault("stop_lon", "0"),
                    CultureInfo.InvariantCulture,
                    out var lon
                )
                    ? lon
                    : 0.0,
                CodMode = codMode,
                FullStopCode = CodeUtils.CreateStopCode(codMode.ToString(), stopCode),
                Wheelchair = int.TryParse(
                    data.GetValueOrDefault("wheelchair_boarding", "0"),
                    out var w
                )
                    ? w
                    : 0,
                Zone = data.GetValueOrDefault("zone_id", ""),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing stop");
            return null;
        }
    }

    public static TransitRoute? ParseRouteFromGtfs(
        Dictionary<string, string> data,
        int feedCodMode,
        ILogger logger
    )
    {
        try
        {
            var routeId = data.GetValueOrDefault("route_id", "");

            return new TransitRoute
            {
                FullLineCode = routeId,
                SimpleLineCode = data.GetValueOrDefault("route_short_name", "")
                    .ToUpper(CultureInfo.InvariantCulture),
                RouteName = data.GetValueOrDefault("route_long_name", ""),
                CodMode = feedCodMode.ToString(),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing route from GTFS");
            return null;
        }
    }

    public static TransitRoute? ParseRouteFromCsv(Dictionary<string, string> data, ILogger logger)
    {
        try
        {
            return new TransitRoute
            {
                FullLineCode = data.GetValueOrDefault("IDFLINEA", ""),
                SimpleLineCode = data.GetValueOrDefault("CODIGOGESTIONLINEA", "")
                    .ToUpper(CultureInfo.InvariantCulture),
                RouteName = data.GetValueOrDefault("DENOMINACION", ""),
                CodMode = data.GetValueOrDefault("MODO", ""),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing route from CSV");
            return null;
        }
    }

    public static StopInfo? ParseStopInfo(Dictionary<string, string> data, ILogger logger)
    {
        try
        {
            return new StopInfo
            {
                IdEstacion = data.GetValueOrDefault("IDESTACION", ""),
                CodigoEmpresa = data.GetValueOrDefault("CODIGOEMPRESA", ""),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing stop info");
            return null;
        }
    }

    public static Itinerary? ParseItineraryFromGtfs(Dictionary<string, string> data, ILogger logger)
    {
        try
        {
            return new Itinerary
            {
                ItineraryCode = data.GetValueOrDefault("shape_id", ""),
                Direction = int.TryParse(data.GetValueOrDefault("direction_id", "0"), out var d)
                    ? d
                    : 0,
                FullLineCode = data.GetValueOrDefault("route_id", ""),
                TripId = data.GetValueOrDefault("trip_id", ""),
                ServiceId = data.GetValueOrDefault("service_id", "UNKNOWN"),
                TripName = data.GetValueOrDefault("trip_short_name", "UNKNOWN"),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing itinerary from GTFS");
            return null;
        }
    }

    public static Itinerary? ParseItineraryFromCsv(Dictionary<string, string> data, ILogger logger)
    {
        try
        {
            var direction = int.TryParse(data.GetValueOrDefault("SENTIDO", "1"), out var s)
                ? s - 1
                : 0;

            return new Itinerary
            {
                ItineraryCode = data.GetValueOrDefault("CODIGOITINERARIO", ""),
                Direction = direction,
                FullLineCode = data.GetValueOrDefault("IDFLINEA", ""),
                TripId = data.GetValueOrDefault("CODIGOITINERARIO", ""),
                ServiceId = "UNKNOWN",
                TripName = "UNKNOWN",
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing itinerary from CSV");
            return null;
        }
    }

    public static Shape? ParseShape(Dictionary<string, string> data, ILogger logger)
    {
        try
        {
            return new Shape
            {
                ItineraryId = data.GetValueOrDefault("shape_id", ""),
                Latitude = double.TryParse(
                    data.GetValueOrDefault("shape_pt_lat", "0"),
                    CultureInfo.InvariantCulture,
                    out var lat
                )
                    ? lat
                    : 0.0,
                Longitude = double.TryParse(
                    data.GetValueOrDefault("shape_pt_lon", "0"),
                    CultureInfo.InvariantCulture,
                    out var lon
                )
                    ? lon
                    : 0.0,
                Sequence = int.TryParse(
                    data.GetValueOrDefault("shape_pt_sequence", "0"),
                    out var seq
                )
                    ? seq
                    : 0,
                Distance = double.TryParse(
                    data.GetValueOrDefault("shape_dist_traveled", "0"),
                    CultureInfo.InvariantCulture,
                    out var dist
                )
                    ? dist
                    : 0.0,
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing shape");
            return null;
        }
    }

    public static StopOrder? ParseStopOrderFromGtfs(
        Dictionary<string, string> data,
        int feedCodMode,
        ILogger logger
    )
    {
        try
        {
            var stopId = data.GetValueOrDefault("stop_id", "");
            var fullStopCode = stopId.StartsWith("par_")
                ? stopId["par_".Length..] // par_8_09568 → 8_09568
                : $"{feedCodMode}_{stopId}"; // 73 → 6_73

            return new StopOrder
            {
                FullStopCode = fullStopCode,
                Order = int.TryParse(data.GetValueOrDefault("stop_sequence", "0"), out var o)
                    ? o
                    : 0,
                TripId = data.GetValueOrDefault("trip_id", ""),
                DepartureTime = ParseDepartureTime(data.GetValueOrDefault("departure_time", "")),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing stop order from GTFS");
            return null;
        }
    }

    public static StopOrder? ParseStopOrderFromCsv(Dictionary<string, string> data, ILogger logger)
    {
        try
        {
            return new StopOrder
            {
                FullStopCode = data.GetValueOrDefault("IDFESTACION", ""),
                Order = int.TryParse(data.GetValueOrDefault("NUMEROORDEN", "0"), out var o) ? o : 0,
                TripId = data.GetValueOrDefault("CODIGOITINERARIO", ""),
                DepartureTime = 0,
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing stop order from CSV");
            return null;
        }
    }

    public static Data.Entities.Calendar? ParseCalendar(
        Dictionary<string, string> data,
        ILogger logger
    )
    {
        try
        {
            return new Data.Entities.Calendar
            {
                ServiceId = data.GetValueOrDefault("service_id", ""),
                Monday = data.GetValueOrDefault("monday", "0") == "1",
                Tuesday = data.GetValueOrDefault("tuesday", "0") == "1",
                Wednesday = data.GetValueOrDefault("wednesday", "0") == "1",
                Thursday = data.GetValueOrDefault("thursday", "0") == "1",
                Friday = data.GetValueOrDefault("friday", "0") == "1",
                Saturday = data.GetValueOrDefault("saturday", "0") == "1",
                Sunday = data.GetValueOrDefault("sunday", "0") == "1",
                StartDate = ParseDateToEpochMs(data.GetValueOrDefault("start_date", "")),
                EndDate = ParseDateToEpochMs(data.GetValueOrDefault("end_date", "")),
            };
        }
        catch (Exception ex)
        {
            if (logger.IsEnabled(LogLevel.Error))
                logger.LogError(ex, "Error parsing calendar");
            return null;
        }
    }

    private static long ParseDepartureTime(string? timeStr)
    {
        if (string.IsNullOrWhiteSpace(timeStr))
            return 0;

        var parts = timeStr.Split(':');
        if (parts.Length != 3)
            return 0;

        var hour = int.Parse(parts[0]);
        var daysToAdd = 0;
        if (hour >= 24)
        {
            hour -= 24;
            daysToAdd++;
        }

        var time = new TimeSpan(daysToAdd, hour, int.Parse(parts[1]), int.Parse(parts[2]));
        return (long)time.TotalMilliseconds;
    }

    private static long ParseDateToEpochMs(string dateStr)
    {
        if (string.IsNullOrWhiteSpace(dateStr))
            return 0;
        var date = DateTime.ParseExact(
            dateStr,
            "yyyyMMdd",
            CultureInfo.InvariantCulture,
            DateTimeStyles.AssumeUniversal | DateTimeStyles.AdjustToUniversal
        );
        return new DateTimeOffset(date, TimeSpan.Zero).ToUnixTimeMilliseconds();
    }
}
