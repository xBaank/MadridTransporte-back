package busTrackerApi.routing.stops.train

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildCachedJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.JsonNode


suspend fun Call.getTrainTimes() = getTrainTimesBase(::getTrainTimesResponse, call.parameters.getWrapped("stopCode"))
suspend fun Call.getTrainTimesCached() = getTrainTimesBase(::getTrainTimesResponseCached, call.parameters.getWrapped("stopCode"))

private suspend fun getTrainTimesBase(
    f: suspend (String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    originId: Either<BusTrackerException, String>,
) = either {
    val stopCodeOrigin = createStopCode(trainCodMode, originId.bind())
    val json = f(stopCodeOrigin).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}