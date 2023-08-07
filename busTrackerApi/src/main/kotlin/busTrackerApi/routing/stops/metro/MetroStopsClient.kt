package busTrackerApi.routing.stops.metro

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildCachedJson
import busTrackerApi.routing.stops.getStopCodeStationById
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.JsonNode

suspend fun Call.getMetroTimes(codMode: String) = getMetroTimesBase(::getTimesByQuery, codMode, call.parameters.getWrapped("stopCode"))
suspend fun Call.getMetroTimesCached(codMode: String) = getMetroTimesBase(::getTimesByQueryCached, codMode, call.parameters.getWrapped("stopCode"))

private suspend fun getMetroTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    codMode: String,
    id: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(codMode, id.bind())
    val stationCode = getStopCodeStationById(stopCode).bind()
    val json = f(stationCode, codMode).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}


