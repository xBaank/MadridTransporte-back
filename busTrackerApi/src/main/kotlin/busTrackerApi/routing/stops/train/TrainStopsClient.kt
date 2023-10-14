package busTrackerApi.routing.stops.train

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.db.getIdByStopCode
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.JsonNode


suspend fun Call.getTrainTimes() = getTrainTimesBase(
    ::getTrainTimesResponse,
    call.request.queryParameters.getWrapped("originStopCode"),
    call.request.queryParameters.getWrapped("destinationStopCode")
)

private suspend fun getTrainTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, JsonNode>,
    originId: Either<BusTrackerException, String>,
    destinationId: Either<BusTrackerException, String>
) = either {
    val stopCodeOrigin = createStopCode(trainCodMode, originId.bind())
    val stopCodeDestination = createStopCode(trainCodMode, destinationId.bind())
    val stopInfoOriginStationCode = getIdByStopCode(stopCodeOrigin).bind()
    val stopInfoDestinationStationCode = getIdByStopCode(stopCodeDestination).bind()
    val json = f(
        stopInfoOriginStationCode,
        stopInfoDestinationStationCode
    ).bind()
    ResponseJson(json, HttpStatusCode.OK)
}