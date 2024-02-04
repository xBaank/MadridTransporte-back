package api.routing.stops

import api.db.*
import api.db.models.LineDestination
import api.db.models.toDeviceToken
import api.extensions.bindJson
import api.routing.Response.*
import api.utils.Pipeline
import arrow.core.continuations.either
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import kotlinx.coroutines.flow.toList
import simpleJson.*
import api.db.getAllStops as getAllStopsFromDb


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