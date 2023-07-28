package busTrackerApi.routing.stops.metro

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.CloseSocketException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.toCloseSocketException
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildCachedJson
import busTrackerApi.routing.stops.getStopById
import busTrackerApi.routing.stops.subscribedStops
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import simpleJson.JsonNode
import simpleJson.asString
import simpleJson.get
import simpleJson.serialized
import kotlin.collections.set
import kotlin.time.Duration.Companion.minutes

suspend fun Call.getMetroTimes() = getMetroTimesBase(::getTimesByQuery, call.parameters.getWrapped("stopCode"))
suspend fun Call.getMetroTimesCached() = getMetroTimesBase(::getTimesByQueryCached,call.parameters.getWrapped("stopCode"))

private suspend fun getMetroTimesBase(
    f: suspend (String) -> Either<BusTrackerException, TimedCachedValue<JsonNode>>,
    id: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(metroCodMode, id.bind())
    val stopInfo = getStopById(stopCode).bind()
    val json = f(stopInfo["name"].asString().bindMap()).bind()
    ResponseJson(buildCachedJson(json.value, json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}

suspend fun WebSocketServerSession.subscribeMetroStopsTimes() = either {
    val stopCode = createStopCode(metroCodMode, call.parameters
        .getWrapped("stopCode")
        .toCloseSocketException(CloseReason.Codes.CANNOT_ACCEPT)
        .bind())
    val ip = call.request.origin.remoteAddress
    val subId = "$ip-$stopCode"

    if (subscribedStops.containsKey(subId)) shift<Nothing>(
        CloseSocketException("Already subscribed to stop code $stopCode", CloseReason.Codes.VIOLATED_POLICY)
    )

    try {
        subscribedStops[subId] = this@subscribeMetroStopsTimes
        while (isActive) {
            val response = getMetroTimesBase(::getTimesByQuery,  call.parameters.getWrapped("stopCode"))
                .toCloseSocketException(CloseReason.Codes.INTERNAL_ERROR)
                .bind()

            send(response.json.serialized())
            delay(1.minutes)
        }
    } finally {
        subscribedStops.remove(subId)
    }
}


