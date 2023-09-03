package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.Response.ResponseRaw
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import simpleJson.*


suspend fun getAlertsByCodMode(codMode: String) = either {
    val alerts = getAlertsByCodModeResponse(codMode).bind()
    ResponseJson(buildAlertsJson(alerts.value), HttpStatusCode.OK)
}

suspend fun Call.getAllStops() = either {
    val stops = getAllStopsResponse().bind()
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseJson(stops, HttpStatusCode.OK)
}

suspend fun Call.getStopTimes(codMode: String) =
    getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getTimesResponse)

suspend fun Call.getStopTimesCached(codMode: String) =
    getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getTimesResponseCached)

private suspend fun getStopTimesBase(
    codMode: String,
    simpleStopCode: Either<BusTrackerException, String>,
    f: suspend (String) -> Either<BusTrackerException, TimedCachedValue<StopTimes>>
) = either {
    val stopCode = createStopCode(codMode, simpleStopCode.bind())
    checkStopExists(stopCode).bind()
    val cached = f(stopCode).bind()
    val json = buildCachedJson(buildJson(cached.value), cached.createdAt.toEpochMilli())
    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.subscribeStopTime(codMode: String, f: suspend (String) -> Either<BusTrackerException, StopTimes>) =
    either {
        val body = call.receiveText().deserialized().bindMap()
        val deviceToken = body["deviceToken"].asString().bindMap()
        val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
        val line = body["line"].asString().bindMap()
        subscribeLineDevice(deviceToken, stopCode, line, codMode) { f(stopCode) }
        ResponseRaw(HttpStatusCode.OK)
    }

suspend fun Call.getSubscriptions(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
    val subscription = getSubscriptionsByStopCode(deviceToken, stopCode) ?: shift<Nothing>(
        BusTrackerException.NotFound("No subscriptions found for device $deviceToken and stop $stopCode")
    )
    ResponseJson(jObject {
        "stopCode" += subscription.stopCode
        "lines" += subscription.lines.map(String::asJson).asJson()
        "codMode" += subscription.codMode.toInt()
    }, HttpStatusCode.OK)
}

suspend fun Call.unsubscribeStopTime(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
    val line = body["line"].asString().bindMap()
    unsubscribeLineDevice(deviceToken, line, stopCode)
    ResponseRaw(HttpStatusCode.OK)
}

suspend fun Call.unsubscribeAllStopTime(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    unsubscribeAllDevice(deviceToken)
    ResponseRaw(HttpStatusCode.OK)
}