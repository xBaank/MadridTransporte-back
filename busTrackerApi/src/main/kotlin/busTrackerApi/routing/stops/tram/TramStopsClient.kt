package busTrackerApi.routing.stops.tram

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildCachedJson
import busTrackerApi.routing.stops.getStopById
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.JsonNode
import simpleJson.asString
import simpleJson.get

suspend fun Call.getTramTimes() = getTramTimesBase(::getTramTimesResponse, call.parameters.getWrapped("stopCode"))
suspend fun Call.getTramTimesCached() = getTramTimesBase(::getTramTimesResponseCached, call.parameters.getWrapped("stopCode"))

private suspend fun getTramTimesBase(
    f: suspend (String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    id: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(tramCodMode, id.bind())
    val stop = getStopById(stopCode).bind()
    val json = f(stop["name"].asString().bindMap()).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}