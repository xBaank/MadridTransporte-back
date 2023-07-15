package busTrackerApi.routing.bus.stops

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.jObject

suspend fun Call.getLocations() = either {
    val latitude = call.request.queryParameters.getWrapped("latitude").bind().toDoubleOrNull() ?:
        shift<Nothing>(BadRequest("Latitude must be a double"))
    val longitude = call.request.queryParameters.getWrapped("longitude").bind().toDoubleOrNull() ?:
        shift<Nothing>(BadRequest("Longitude must be a double"))

    val stops = getStopsByLocation(latitude, longitude).bind()

    ResponseJson(buildStopLocationsJson(stops), HttpStatusCode.OK)
}

suspend fun Call.getEstimations() = either {
    val stopCode = createStopCode("8", call.parameters.getWrapped("stopCode").bind())

    val estimationsVCached = getEstimations(stopCode).bind()

    val json = jObject {
        "data" += estimationsVCached.value
        "lastTime" += estimationsVCached.createdAt.toEpochMilli()
    }

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getStopTimes() = either {
    val stopCode = createStopCode("8", call.parameters.getWrapped("stopCode").bind())
    val codMode = call.request.queryParameters.getWrapped("codMode").bind()

    val timedVCached = getTimesOrCached(stopCode, codMode).bind()

    val json = jObject {
        "data" += timedVCached.value
        "lastTime" += timedVCached.createdAt.toEpochMilli()
    }

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getStopTimesCached() = either {
    val stopCode = createStopCode("8", call.parameters.getWrapped("stopCode").bind())
    val cached = stopTimesCache.get(stopCode) ?: shift<Nothing>(NotFound("Stop code not found"))

    val json = jObject {
        "data" += cached.value
        "lastTime" += cached.createdAt.toEpochMilli()
    }

    ResponseJson(json, HttpStatusCode.OK)
}