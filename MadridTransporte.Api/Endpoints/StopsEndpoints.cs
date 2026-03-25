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

        stops.MapGet(
            "/all",
            async (StopsService stopsService, CancellationToken ct) =>
            {
                var result = await stopsService.GetAllStopsAsync(ct);
                return Results.Ok(result);
            }
        );

        // Bus
        MapStopTimesEndpoints(
            stops,
            "/bus",
            CodeUtils.BusCodMode,
            async (fullStopCode, sp, ct) =>
                await sp.GetRequiredService<BusClient>().GetStopTimesAsync(fullStopCode, ct)
        );

        // EMT
        MapStopTimesEndpoints(
            stops,
            "/emt",
            CodeUtils.EmtCodMode,
            async (fullStopCode, sp, ct) =>
                await sp.GetRequiredService<EmtClient>().GetStopTimesAsync(fullStopCode, ct)
        );

        // Metro
        MapStopTimesEndpoints(
            stops,
            "/metro",
            CodeUtils.MetroCodMode,
            async (fullStopCode, sp, ct) =>
                await sp.GetRequiredService<MetroClient>().GetStopTimesAsync(fullStopCode, ct)
        );

        // Tram (uses CRTM)
        MapStopTimesEndpoints(
            stops,
            "/tram",
            CodeUtils.TramCodMode,
            async (fullStopCode, sp, ct) =>
                await sp.GetRequiredService<CrtmClient>().GetStopTimesAsync(fullStopCode, ct)
        );

        // Train
        MapStopTimesEndpoints(
            stops,
            "/train",
            CodeUtils.TrainCodMode,
            async (fullStopCode, sp, ct) =>
                await sp.GetRequiredService<TrainClient>().GetStopTimesAsync(fullStopCode, ct)
        );

        // Train routed
        stops.MapGet(
            "/train/times",
            async (
                string originStopCode,
                string destinationStopCode,
                StopsService stopsService,
                TrainClient trainClient,
                CancellationToken ct
            ) =>
            {
                var originFullCode = CodeUtils.CreateStopCode(
                    CodeUtils.TrainCodMode,
                    originStopCode
                );
                var destFullCode = CodeUtils.CreateStopCode(
                    CodeUtils.TrainCodMode,
                    destinationStopCode
                );
                await stopsService.GetStopByFullStopCodeAsync(originFullCode, ct);
                await stopsService.GetStopByFullStopCodeAsync(destFullCode, ct);

                var result = await trainClient.GetRoutedTimesAsync(
                    originStopCode,
                    destinationStopCode,
                    ct
                );
                return result != null ? Results.Ok(result) : Results.StatusCode(503);
            }
        );
    }

    private static void MapStopTimesEndpoints(
        RouteGroupBuilder group,
        string prefix,
        string codMode,
        Func<string, IServiceProvider, CancellationToken, Task<StopTimesDto?>> getTimesFunc
    )
    {
        var modeGroup = group.MapGroup(prefix);

        modeGroup.MapGet(
            "/{stopCode}/times",
            async (
                string stopCode,
                StopsService stopsService,
                IServiceProvider sp,
                CancellationToken ct
            ) =>
            {
                var fullStopCode = CodeUtils.CreateStopCode(codMode, stopCode);
                await stopsService.GetStopByFullStopCodeAsync(fullStopCode, ct); // validates existence

                var times = await getTimesFunc(fullStopCode, sp, ct);
                if (times == null)
                    return Results.StatusCode(503);

                return times.Arrives != null
                    ? Results.Ok(times)
                    : Results.Json(times, statusCode: 503);
            }
        );

        modeGroup.MapGet(
            "/{stopCode}/planned",
            async (string stopCode, StopsService stopsService, CancellationToken ct) =>
            {
                var fullStopCode = CodeUtils.CreateStopCode(codMode, stopCode);
                await stopsService.GetStopByFullStopCodeAsync(fullStopCode, ct);
                var planned = await stopsService.GetStopTimesPlannedAsync(fullStopCode, ct);
                return Results.Ok(planned);
            }
        );

        modeGroup.MapGet(
            "/alerts",
            async (CrtmClient crtmClient, CancellationToken ct) =>
            {
                var alerts = await crtmClient.GetAlertsAsync(codMode, ct);
                return Results.Ok(alerts);
            }
        );
    }
}
