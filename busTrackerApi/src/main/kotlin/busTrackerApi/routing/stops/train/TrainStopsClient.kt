package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*

suspend fun Call.getTrainTimes() = either {
    val stopCode = call.parameters.getWrapped("stopCode").bind()
    val json = getTrainTimes(createStopCode(trainCodMode,stopCode)).bind()
    ResponseJson(json, HttpStatusCode.OK)
}