package busTrackerApi.routing.stops.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildCachedJson
import busTrackerApi.utils.Call
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.JsonNode

suspend fun Call.getStopTimes() = getStopTimesBase(::getStopTimesResponse, call.parameters.getWrapped("stopCode"))

suspend fun Call.getStopTimesCached() = getStopTimesBase(::getStopTimesResponseCached, call.parameters.getWrapped("stopCode"))

private suspend fun getStopTimesBase(
    f: suspend (String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    id: Either<BusTrackerException, String>
)  = either {
    val json = f(id.bind()).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}