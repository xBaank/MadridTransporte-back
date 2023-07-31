package busTrackerApi.routing.stops.train

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


suspend fun Call.getTrainTimes() = getTrainTimesBase(::getTrainTimesResponse, call.request.queryParameters.getWrapped("originStopCode"), call.request.queryParameters.getWrapped("destinationStopCode"))
suspend fun Call.getTrainTimesCached() = getTrainTimesBase(::getTrainTimesResponseCached, call.request.queryParameters.getWrapped("originStopCode"), call.request.queryParameters.getWrapped("destinationStopCode"))

private suspend fun getTrainTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    originId: Either<BusTrackerException, String>,
    destinationId: Either<BusTrackerException, String>
) = either {
    val json = f(originId.bind(), destinationId.bind()).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}