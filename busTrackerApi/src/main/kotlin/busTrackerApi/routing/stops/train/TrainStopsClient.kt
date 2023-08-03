package busTrackerApi.routing.stops.train

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
import simpleJson.asNumber
import simpleJson.get


suspend fun Call.getTrainTimes() = getTrainTimesBase(::getTrainTimesResponse, call.request.queryParameters.getWrapped("originStopCode"), call.request.queryParameters.getWrapped("destinationStopCode"))
suspend fun Call.getTrainTimesCached() = getTrainTimesBase(::getTrainTimesResponseCached, call.request.queryParameters.getWrapped("originStopCode"), call.request.queryParameters.getWrapped("destinationStopCode"))

private suspend fun getTrainTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    originId: Either<BusTrackerException, String>,
    destinationId: Either<BusTrackerException, String>
) = either {
    val stopCodeOrigin = createStopCode(trainCodMode, originId.bind())
    val stopCodeDestination = createStopCode(trainCodMode, destinationId.bind())
    val stopInfoOrigin = getStopById(stopCodeOrigin).bind()
    val stopInfoDestination = getStopById(stopCodeDestination).bind()
    val json = f(
        stopInfoOrigin["CODIGOEMPRESA"].asNumber().bindMap().toString(),
        stopInfoDestination["CODIGOEMPRESA"].asNumber().bindMap().toString()
    ).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}