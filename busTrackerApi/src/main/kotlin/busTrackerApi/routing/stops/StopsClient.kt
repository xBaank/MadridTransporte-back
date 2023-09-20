package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.*
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import simpleJson.*

suspend fun getAlertsByCodMode(codMode: String) = either {
    val alerts = getAlertsByCodModeResponse(codMode).bind()
    ResponseJsonCached(buildAlertsJson(alerts), HttpStatusCode.OK)
}

suspend fun getAllStops() = either {
    val stops = getAllStopsResponse().bind()
    ResponseJsonCached(stops, HttpStatusCode.OK)
}

suspend fun Call.getStopTimes(codMode: String) =
    getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getBusTimesResponse)

private suspend fun getStopTimesBase(
    codMode: String,
    simpleStopCode: Either<BusTrackerException, String>,
    f: suspend (String) -> Either<BusTrackerException, StopTimes>
) = either {
    val stopCode = createStopCode(codMode, simpleStopCode.bind())
    checkStopExists(stopCode).bind()
    val times = f(stopCode).bind()
    val statusCode = if (times.arrives == null) HttpStatusCode.BadRequest else HttpStatusCode.OK
    ResponseJson(buildStopTimesJson(times), statusCode)
}

suspend fun Call.subscribeStopTime(codMode: String) =
    either {
        val body = call.receiveText().deserialized().bindMap()
        val deviceToken = body["deviceToken"].asString().bindMap()
        val subscription = body["subscription"].bindMap()
        val stopCode = createStopCode(codMode, subscription["stopCode"].asString().bindMap())
        checkStopExists(stopCode).bind()
        val lineDestination = LineDestination(
            subscription["lineDestination"]["line"].asString().bindMap(),
            subscription["lineDestination"]["destination"].asString().bindMap(),
            subscription["lineDestination"]["codMode"].asInt().bindMap()
        )
        subscribeDevice(
            deviceToken = deviceToken,
            stopId = stopCode,
            codMode = codMode,
            lineDestination = lineDestination
        ).bind()

        ResponseRaw(HttpStatusCode.OK)
    }

suspend fun Call.getSubscription(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val stopCode = createStopCode(codMode, body["stopCode"].asString().bindMap())
    val subscription = getSubscription(deviceToken = deviceToken, stopCode = stopCode).bind()
    ResponseJson(buildSubscription(subscription, deviceToken), HttpStatusCode.OK)
}

suspend fun Call.getAllSubscriptions() = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val subscriptions = getSubscriptions(deviceToken)
    ResponseJson(subscriptions.map { buildSubscription(it, deviceToken) }.asJson(), HttpStatusCode.OK)
}

suspend fun Call.unsubscribeStopTime(codMode: String) = either {
    val body = call.receiveText().deserialized().bindMap()
    val deviceToken = body["deviceToken"].asString().bindMap()
    val subscription = body["subscription"].bindMap()
    val stopCode = createStopCode(codMode, subscription["stopCode"].asString().bindMap())
    val lineDestination = LineDestination(
        subscription["lineDestination"]["line"].asString().bindMap(),
        subscription["lineDestination"]["destination"].asString().bindMap(),
        subscription["lineDestination"]["codMode"].asInt().bindMap()
    )
    unsubscribeDevice(deviceToken = deviceToken, stopCode = stopCode, lineDestination = lineDestination)
    ResponseRaw(HttpStatusCode.OK)
}