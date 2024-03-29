package api.routing.stops.bus

import api.config.EnvVariables
import api.db.getCoordinatesByStopCode
import api.db.getStopNameByStopCode
import api.extensions.getSuspend
import api.utils.auth
import api.utils.defaultClient
import api.utils.mapExceptionsF
import arrow.core.Either
import arrow.core.raise.either
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

suspend fun getCRTMStopTimes(stopCode: String) = either {
    val stopTimes = withTimeoutOrNull(EnvVariables.timeoutSeconds) {
        getStopTimesResponse(stopCode).getOrNull()
    }

    val result = extractCRTMStopTimes(
        stopTimes,
        getCoordinatesByStopCode(stopCode).bind(),
        getStopNameByStopCode(stopCode).getOrNull(),
        getStopCodeFromFullStopCode(stopCode)
    )

    result
}