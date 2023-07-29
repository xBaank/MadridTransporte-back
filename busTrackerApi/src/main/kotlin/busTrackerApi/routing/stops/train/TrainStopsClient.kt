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
suspend fun Call.getTrainTimesCached() = getTrainTimesBase(::getTrainTimesCachedResponse, call.parameters.getWrapped("stopCode"))

suspend fun getTrainTimesBase(
    f: suspend (String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    id: Either<BusTrackerException, String>
) = either {
    val json = f(createStopCode(trainCodMode, id.bind())).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}

