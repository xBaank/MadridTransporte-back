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
import io.ktor.server.application.*
import io.ktor.server.request.*
import simpleJson.*


suspend fun getAlertsByCodMode(codMode: String) = either {
    val alerts = getAlertsByCodModeResponse(codMode).bind()
    ResponseJson(buildAlertsJson(alerts.value), HttpStatusCode.OK)
}

suspend fun Call.getAllStops() = either {
    val stops = getAllStopsResponse().bind()
    ResponseJson(stops, HttpStatusCode.OK)
}

suspend fun Call.getStopTimes(codMode: String) =
    getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getBusTimesResponse)

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

suspend fun Call.subscribeStopTime(codMode: String) =
    either {
        val body = call.receiveText().deserialized().bindMap()
        val deviceToken = body["deviceToken"].asString().bindMap()
        val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
        subscribeDevice(deviceToken = deviceToken, stopId = stopCode, codMode = codMode)
        ResponseRaw(HttpStatusCode.OK)
    }

suspend fun Call.getSubscriptions(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
    val stopCodes =
        getSubscriptionsByStopCode(deviceToken = deviceToken, stopCode = stopCode).map { it.stopCode.asJson() }
    ResponseJson(jObject {
        "stopCodes" += stopCodes.asJson()
        "codMode" += codMode.toInt().asJson()
    }, HttpStatusCode.OK)
}

suspend fun Call.unsubscribeStopTime(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
    unsubscribeDevice(deviceToken, stopCode)
    ResponseRaw(HttpStatusCode.OK)
}

suspend fun Call.unsubscribeAllStopTime(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    unsubscribeAllDevice(deviceToken)
    ResponseRaw(HttpStatusCode.OK)
}