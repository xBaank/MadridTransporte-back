package busTrackerApi.routing.bus.stops

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.NotFound
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
    val codMode = call.request.queryParameters.getWrapped("codMode").getOrNull()

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

suspend fun WebSocketServerSession.subscribeStopsTimes() = either {
    val stopCode = createStopCode("8", call.parameters
        .getWrapped("stopCode")
        .toCloseSocketException(CloseReason.Codes.CANNOT_ACCEPT)
        .bind())
    val email = call.principal<JWTPrincipal>()
        .getWrapped("email")
        .toCloseSocketException(CloseReason.Codes.CANNOT_ACCEPT)
        .bind()
    val codMode = call.request.queryParameters.getWrapped("codMode").getOrNull()
    val ip = call.request.origin.remoteAddress
    val subId = "$ip-$email-$stopCode"

    if (subscribedStops.containsKey(subId)) shift<Nothing>(
        CloseSocketException("Already subscribed to stop code $stopCode", CloseReason.Codes.VIOLATED_POLICY)
    )

    try {
        subscribedStops[subId] = this@subscribeStopsTimes
        while (isActive) {
            val timedVCached = getTimesOrCached(stopCode, codMode)
                .toCloseSocketException(CloseReason.Codes.INTERNAL_ERROR)
                .bind()

            val json = jObject {
                "data" += timedVCached.value
                "lastTime" += timedVCached.createdAt.toEpochMilli()
            }

            send(json.serialized())
            delay(1.minutes)
        }
    } finally {
        subscribedStops.remove(subId)
    }
}