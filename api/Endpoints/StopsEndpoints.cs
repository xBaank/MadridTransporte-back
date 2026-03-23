using MadridTransporte.Api.Clients.Bus;
using MadridTransporte.Api.Clients.Crtm;
using MadridTransporte.Api.Clients.Emt;
using MadridTransporte.Api.Clients.Metro;
using MadridTransporte.Api.Clients.Train;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Api.Exceptions;
using MadridTransporte.Api.Services;
using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Endpoints;

public static class StopsEndpoints
{
    public static void MapStopsEndpoints(this WebApplication app)
    {
        var stops = app.MapGroup("/stops");

        stops.MapGet("/all", async (IStopsService stopsService) =>
        {
            var result = await stopsService.GetAllStopsAsync();
            return Results.Ok(result);
        });

        // Bus
        MapStopTimesEndpoints(stops, "/bus", CodeUtils.BusCodMode,
            async (fullStopCode, sp) => await sp.GetRequiredService<IBusClient>().GetStopTimesAsync(fullStopCode));

        // EMT
        MapStopTimesEndpoints(stops, "/emt", CodeUtils.EmtCodMode,
            async (fullStopCode, sp) => await sp.GetRequiredService<IEmtClient>().GetStopTimesAsync(fullStopCode));

        // Metro
        MapStopTimesEndpoints(stops, "/metro", CodeUtils.MetroCodMode,
            async (fullStopCode, sp) => await sp.GetRequiredService<IMetroClient>().GetStopTimesAsync(fullStopCode));

        // Tram (uses CRTM)
        MapStopTimesEndpoints(stops, "/tram", CodeUtils.TramCodMode,
            async (fullStopCode, sp) => await sp.GetRequiredService<ICrtmClient>().GetStopTimesAsync(fullStopCode));

        // Train
        MapStopTimesEndpoints(stops, "/train", CodeUtils.TrainCodMode,
            async (fullStopCode, sp) => await sp.GetRequiredService<ITrainClient>().GetStopTimesAsync(fullStopCode));

        // Train routed
        stops.MapGet("/train/times", async (string originStopCode, string destinationStopCode, ITrainClient trainClient) =>
        {
            var result = await trainClient.GetRoutedTimesAsync(originStopCode, destinationStopCode);
            return result != null ? Results.Ok(result) : Results.StatusCode(503);
        });
    }

    private static void MapStopTimesEndpoints(RouteGroupBuilder group, string prefix, string codMode,
        Func<string, IServiceProvider, Task<StopTimesDto?>> getTimesFunc)
    {
        var modeGroup = group.MapGroup(prefix);

        modeGroup.MapGet("/{stopCode}/times", async (string stopCode, IStopsService stopsService, IServiceProvider sp) =>
        {
            var fullStopCode = CodeUtils.CreateStopCode(codMode, stopCode);
            await stopsService.GetStopByFullStopCodeAsync(fullStopCode); // validates existence

            var times = await getTimesFunc(fullStopCode, sp);
            if (times == null) return Results.StatusCode(503);

            return times.Arrives != null ? Results.Ok(times) : Results.Json(times, statusCode: 503);
        });

        modeGroup.MapGet("/{stopCode}/planned", async (string stopCode, IStopsService stopsService) =>
        {
            var fullStopCode = CodeUtils.CreateStopCode(codMode, stopCode);
            await stopsService.GetStopByFullStopCodeAsync(fullStopCode);
            var planned = await stopsService.GetStopTimesPlannedAsync(fullStopCode);
            return Results.Ok(planned);
        });

        modeGroup.MapGet("/alerts", async (ICrtmClient crtmClient) =>
        {
            var alerts = await crtmClient.GetAlertsAsync(codMode);
            return Results.Ok(alerts);
        });
    }
}
