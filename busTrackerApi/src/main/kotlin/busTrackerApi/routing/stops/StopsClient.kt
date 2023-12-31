package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.db.*
import busTrackerApi.db.models.LineDestination
import busTrackerApi.db.models.toDeviceToken
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindJson
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.*
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import kotlinx.coroutines.flow.toList
import simpleJson.*
import busTrackerApi.db.getAllStops as getAllStopsFromDb


fun Pipeline.getAllStops(): ResponseFlowJson {
    val stops = getAllStopsFromDb()
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    return ResponseFlowJson(buildStops(stops), HttpStatusCode.OK)
}

suspend fun Pipeline.getAlertsByCodMode(codMode: String) = either {
    val alerts = getAlertsByCodModeResponse(codMode).bind()
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseJsonCached(buildAlertsJson(alerts), HttpStatusCode.OK)
}

suspend fun Pipeline.getStopTimes(codMode: String) =
    getStopTimesBase(codMode, call.parameters.getWrapped("stopCode"), ::getBusTimesResponse)

private suspend fun Pipeline.getStopTimesBase(
    codMode: String,
    simpleStopCode: Either<BusTrackerException, String>,
    f: suspend (String) -> Either<BusTrackerException, StopTimes>
) = either {
    val stopCode = createStopCode(codMode, simpleStopCode.bind())
    checkStopExists(stopCode).bind()
    val times = f(stopCode).bind()
    if (times.arrives != null) call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 30))
    val statusCode = if (times.arrives == null) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK
    ResponseJson(buildStopTimesJson(times), statusCode)
}

suspend fun Pipeline.subscribeStopTime(codMode: String) =
    either {
        val body = call.receiveText().deserialized().bindJson()
        val deviceToken = body["deviceToken"].asString().bindJson()
        val subscription = body["subscription"].bindJson()
        val stopCode = createStopCode(codMode, subscription["stopCode"].asString().bindJson())
        checkStopExists(stopCode).bind()
        val lineDestination = LineDestination(
            subscription["lineDestination"]["line"].asString().bindJson(),
            subscription["lineDestination"]["destination"].asString().bindJson(),
            subscription["lineDestination"]["codMode"].asInt().bindJson()
        )
        subscribeDevice(
            deviceToken = deviceToken.toDeviceToken(),
            stopId = stopCode,
            lineDestination = lineDestination,
            codMode = codMode
        ).bind()

        ResponseRaw(HttpStatusCode.OK)
    }

suspend fun Pipeline.getSubscription(codMode: String) = either {
    val body = call.receiveText().deserialized().bindJson()
    val deviceToken = body["deviceToken"].asString().bindJson().toDeviceToken()
    val stopCode = createStopCode(codMode, body["stopCode"].asString().bindJson())
    val subscription = getSubscription(deviceToken = deviceToken, stopCode = stopCode).bind()
    ResponseJson(buildSubscription(subscription, deviceToken), HttpStatusCode.OK)
}

suspend fun Pipeline.getAllSubscriptions() = either {
    val body = call.receiveText().deserialized().bindJson()
    val deviceToken = body["deviceToken"].asString().bindJson().toDeviceToken()
    val subscriptions = getSubscriptions(deviceToken).toList()
    ResponseJson(subscriptions.map { buildSubscription(it, deviceToken) }.asJson(), HttpStatusCode.OK)
}

suspend fun Pipeline.unsubscribeStopTime(codMode: String) = either {
    val body = call.receiveText().deserialized().bindJson()
    val deviceToken = body["deviceToken"].asString().bindJson()
    val subscription = body["subscription"].bindJson()
    val stopCode = createStopCode(codMode, subscription["stopCode"].asString().bindJson())
    val lineDestination = LineDestination(
        subscription["lineDestination"]["line"].asString().bindJson(),
        subscription["lineDestination"]["destination"].asString().bindJson(),
        subscription["lineDestination"]["codMode"].asInt().bindJson()
    )
    unsubscribeDevice(deviceToken = deviceToken.toDeviceToken(), stopCode = stopCode, lineDestination = lineDestination)
    ResponseRaw(HttpStatusCode.OK)
}