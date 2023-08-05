package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.JsonNode


suspend fun getAlertsByCodMode(codMode: String) = either {
    val alerts = getAlertsByCodModeResponse(codMode).bind()
    ResponseJson(buildAlertsJson(alerts.value), HttpStatusCode.OK)
}

suspend fun getAllStops() = either {
    val stops = getAllStopsResponse().bind()
    ResponseJson(stops, HttpStatusCode.OK)
}

suspend fun Call.getStopTimes(codMode : String) = getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getTimesResponse)
suspend fun Call.getStopTimesCached(codMode : String) = getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getTimesResponseCached)

private suspend fun getStopTimesBase(
    codMode: String,
    stopCode: Either<BusTrackerException, String>,
    f: suspend (String, String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>
) = either {
    val stopCode = createStopCode(codMode, stopCode.bind())
    getStopById(stopCode).bind()
    val cached =  f(stopCode, codMode).bind()
    val json = buildCachedJson(cached.value, cached.createdAt.toEpochMilli())
    ResponseJson(json, HttpStatusCode.OK)
}
