package busTrackerApi.routing.stops

import arrow.core.continuations.either
import arrow.core.getOrElse
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.CloseSocketException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.toCloseSocketException
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import simpleJson.jObject
import simpleJson.serialized
import kotlin.time.Duration.Companion.minutes

suspend fun Call.getStopsByQuery() = either {
    val query = call.request.queryParameters.getWrapped("query").bind()

    val stops = getStopsByQueryResponse(query).bind()

    ResponseJson(buildStopsJson(stops), HttpStatusCode.OK)
}

suspend fun Call.getLocations() = either {
    val latitude = call.request.queryParameters.getWrapped("latitude").bind().toDoubleOrNull() ?:
    shift<Nothing>(BusTrackerException.BadRequest("Latitude must be a double"))
    val longitude = call.request.queryParameters.getWrapped("longitude").bind().toDoubleOrNull() ?:
    shift<Nothing>(BusTrackerException.BadRequest("Longitude must be a double"))

    val stops = getStopsByLocationResponse(latitude, longitude).bind()

    ResponseJson(buildStopLocationsJson(stops), HttpStatusCode.OK)
}

suspend fun Call.getStopTimes(codMode : String) = either {
    val stopCode = createStopCode(codMode, call.parameters.getWrapped("stopCode").bind())

    val timedVCached =  getTimesResponse(stopCode, codMode)
        .onRight { stopTimesCache.put(stopCode, it) }
        .getOrElse { stopTimesCache.get(stopCode) } ?:
        shift<Nothing>(BusTrackerException.NotFound("No stop times found for stop code $stopCode"))

    val json = jObject {
        "data" += timedVCached.value.let(::buildStopTimesJson)
        "lastTime" += timedVCached.createdAt.toEpochMilli()
    }

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getStopTimesCached(codMode: String) = either {
    val stopCode = createStopCode(codMode, call.parameters.getWrapped("stopCode").bind())
    val cached = stopTimesCache.get(stopCode) ?: shift<Nothing>(BusTrackerException.NotFound("Stop code not found"))

    val json = jObject {
        "data" += cached.value.let(::buildStopTimesJson)
        "lastTime" += cached.createdAt.toEpochMilli()
    }

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun WebSocketServerSession.subscribeStopsTimes(codMode : String) = either {
    val stopCode = createStopCode(codMode, call.parameters
        .getWrapped("stopCode")
        .toCloseSocketException(CloseReason.Codes.CANNOT_ACCEPT)
        .bind())
    val email = call.principal<JWTPrincipal>()
        .getWrapped("email")
        .toCloseSocketException(CloseReason.Codes.CANNOT_ACCEPT)
        .bind()

    val ip = call.request.origin.remoteAddress
    val subId = "$ip-$email-$stopCode"

    if (subscribedStops.containsKey(subId)) shift<Nothing>(
        CloseSocketException("Already subscribed to stop code $stopCode", CloseReason.Codes.VIOLATED_POLICY)
    )

    try {
        subscribedStops[subId] = this@subscribeStopsTimes
        while (isActive) {
            val timedVCached = getTimesOrCachedResponse(stopCode, codMode)
                .toCloseSocketException(CloseReason.Codes.INTERNAL_ERROR)
                .bind()

            val json = jObject {
                "data" += timedVCached.value.let(::buildStopTimesJson)
                "lastTime" += timedVCached.createdAt.toEpochMilli()
            }

            send(json.serialized())
            delay(1.minutes)
        }
    } finally {
        subscribedStops.remove(subId)
    }
}