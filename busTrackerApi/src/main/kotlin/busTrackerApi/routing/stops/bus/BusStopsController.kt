package busTrackerApi.routing.stops.bus

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.EnvVariables
import busTrackerApi.db.getCoordinatesByStopCode
import busTrackerApi.db.getStopNameByStopCode
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import crtm.soap.StopTimesRequest
import crtm.utils.getStopCodeFromFullStopCode
import kotlinx.coroutines.withTimeoutOrNull

private suspend fun getStopTimesResponse(stopCode: String) = Either.catch {
    val request = StopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.value().auth()
    }
    getSuspend(request, defaultClient.value()::getStopTimesAsync)
}.mapLeft(mapExceptionsF)

suspend fun getBusStopTimes(stopCode: String) = either {
    val stopTimes = withTimeoutOrNull(EnvVariables.timeoutSeconds) {
        getStopTimesResponse(stopCode).getOrNull()
    }

    val result = extractBusStopTimes(
        stopTimes,
        getCoordinatesByStopCode(stopCode).bind(),
        getStopNameByStopCode(stopCode).getOrNull(),
        getStopCodeFromFullStopCode(stopCode)
    )

    result
}