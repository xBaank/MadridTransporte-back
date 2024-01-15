package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.EnvVariables.timeoutSeconds
import busTrackerApi.db.getCoordinatesByStopCode
import busTrackerApi.db.getStopNameByStopCode
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import crtm.soap.ArrayOfString
import crtm.soap.IncidentsAffectationsRequest
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesRequest
import crtm.utils.getCodStopFromStopCode
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.hours

val cachedAlerts = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, IncidentsAffectationsResponse>()

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

suspend fun getBusTimesResponse(stopCode: String) = either {
    val stopTimes = withTimeoutOrNull(timeoutSeconds) {
        getStopTimesResponse(stopCode).getOrNull()
    }

    val result = parseStopTimesResponseToStopTimes(
        stopTimes,
        getCoordinatesByStopCode(stopCode).bind(),
        getStopNameByStopCode(stopCode).getOrNull(),
        getCodStopFromStopCode(stopCode)
    )

    result
}

suspend fun getAlertsByCodModeResponse(codMode: String) = Either.catch {
    val result = withTimeoutOrNull(timeoutSeconds) {
        val request = IncidentsAffectationsRequest().apply {
            this.codMode = codMode
            codLines = ArrayOfString()
            authentication = defaultClient.value().auth()
        }

        getSuspend(request, defaultClient.value()::getIncidentsAffectationsAsync)
    }

    if (result == null) {
        val cached = cachedAlerts.get(codMode)
        if (cached != null) return@catch cached
        else throw BusTrackerException.InternalServerError("Got empty response")
    }

    cachedAlerts.put(codMode, result)
    result
}.mapLeft(mapExceptionsF)

