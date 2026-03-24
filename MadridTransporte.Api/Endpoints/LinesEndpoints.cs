using MadridTransporte.Api.Clients.Crtm;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Exceptions;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Endpoints;

public static class LinesEndpoints
{
    public static void MapLinesEndpoints(this WebApplication app)
    {
        var lines = app.MapGroup("/lines");

        lines.MapGet("/all", async (IRoutesService routesService) =>
        {
            var routes = await routesService.GetRoutesWithItinerariesAsync();
            return Results.Ok(routes);
        });

        // Bus
        MapLineEndpoints(lines, "/bus", CodeUtils.BusCodMode, hasLocations: true);
        // EMT
        MapLineEndpoints(lines, "/emt", CodeUtils.EmtCodMode, hasLocations: true);
        // Metro
        MapLineEndpoints(lines, "/metro", CodeUtils.MetroCodMode, hasLocations: false);
        // Tram
        MapLineEndpoints(lines, "/tram", CodeUtils.TramCodMode, hasLocations: true);
        // Train
        MapLineEndpoints(lines, "/train", CodeUtils.TrainCodMode, hasLocations: false);
    }

    private static void MapLineEndpoints(RouteGroupBuilder group, string prefix, string codMode, bool hasLocations)
    {
        var modeGroup = group.MapGroup(prefix);

        modeGroup.MapGet("/shapes/{itineraryCode}", async (string itineraryCode, IShapesService shapesService) =>
        {
            var shapes = await shapesService.GetShapesByItineraryCodeAsync(itineraryCode);
            return Results.Ok(shapes);
        });

        modeGroup.MapGet("/{lineCode}/itineraries/{direction:int}", async (
            string lineCode, int direction, string? stopCode,
            IItinerariesService itinerariesService, ICrtmClient crtmClient) =>
        {
            var fullStopCode = stopCode != null ? CodeUtils.CreateStopCode(codMode, stopCode) : null;

            var itinerary = await itinerariesService.GetItineraryByLineAndDirectionAsync(lineCode, direction, fullStopCode)
                ?? await crtmClient.GetLineItinerariesAsync(lineCode, direction);

            return itinerary != null ? Results.Ok(itinerary) : Results.NotFound();
        });

        modeGroup.MapGet("/itineraries/{itineraryCode}", async (string itineraryCode, IItinerariesService itinerariesService) =>
        {
            var itinerary = await itinerariesService.GetItineraryByCodeAsync(itineraryCode);
            return itinerary != null ? Results.Ok(itinerary) : throw ApiException.NotFound("Itinerary code not found");
        });

        modeGroup.MapGet("/itineraries/{itineraryCode}/locations", async (
            string itineraryCode, string? stopCode,
            IItinerariesService itinerariesService, IRoutesService routesService, ICrtmClient crtmClient) =>
        {
            var lineCode = await itinerariesService.GetFullLineCodeByItineraryCodeAsync(itineraryCode);
            if (lineCode == null) throw ApiException.NotFound("Itinerary code not found");

            var lineCodMode = CodeUtils.GetCodModeFromLineCode(lineCode);
            var fullStopCode = stopCode != null ? CodeUtils.CreateStopCode(codMode, stopCode) : null;
            var route = await routesService.GetRouteByFullLineCodeAsync(lineCode);
            var simpleLineCode = route?.SimpleLineCode ?? CodeUtils.GetSimpleLineCodeFromLineCode(lineCode);
            var routeCodMode = route?.CodMode ?? lineCodMode;

            var locations = await crtmClient.GetLineLocationsByItineraryAsync(lineCode, itineraryCode, lineCodMode, fullStopCode);
            if (locations == null) throw ApiException.ServiceUnavailable("Locations unavailable");

            locations.CodMode = int.TryParse(routeCodMode, out var cm) ? cm : 0;
            locations.LineCode = simpleLineCode;
            return Results.Ok(locations);
        });

        if (hasLocations)
        {
            modeGroup.MapGet("/{lineCode}/locations/{direction:int}", async (
                string lineCode, int direction, string? stopCode,
                IRoutesService routesService, ICrtmClient crtmClient) =>
            {
                var lineCodMode = CodeUtils.GetCodModeFromLineCode(lineCode);
                var fullStopCode = stopCode != null ? CodeUtils.CreateStopCode(codMode, stopCode) : null;
                var route = await routesService.GetRouteByFullLineCodeAsync(lineCode);
                var simpleLineCode = route?.SimpleLineCode ?? CodeUtils.GetSimpleLineCodeFromLineCode(lineCode);
                var routeCodMode = route?.CodMode ?? lineCodMode;

                var locations = await crtmClient.GetLineLocationsAsync(lineCode, direction, lineCodMode, fullStopCode);
                if (locations == null) throw ApiException.ServiceUnavailable("Locations unavailable");

                locations.CodMode = int.TryParse(routeCodMode, out var cm) ? cm : 0;
                locations.LineCode = simpleLineCode;
                return Results.Ok(locations);
            });
        }
    }
}
