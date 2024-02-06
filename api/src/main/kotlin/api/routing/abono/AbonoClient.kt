package api.routing.abono

import api.db.addAbonoSubscription
import api.db.getAbonoSubscription
import api.db.models.AbonoSubscription
import api.db.models.toDeviceToken
import api.db.removeAbonoSubscription
import api.exceptions.BusTrackerException.NotFound
import api.extensions.bindJson
import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.routing.Response.ResponseRaw
import api.utils.Pipeline
import arrow.core.continuations.either
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get


suspend fun Pipeline.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val result = getAbonoResponse(id).bind()
    call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}

suspend fun Pipeline.subscribeAbono() = either {
    val body = call.receiveText().deserialized().bindJson()
    val deviceToken = body["deviceToken"].asString().bindJson()
    val ttpNumber = body["ttpNumber"].asString().bindJson()
    getAbonoResponse(ttpNumber).bind()
    addAbonoSubscription(AbonoSubscription(ttpNumber, deviceToken.toDeviceToken()))
    ResponseRaw(HttpStatusCode.OK)
}

suspend fun Pipeline.unsubscribeAbono() = either {
    val body = call.receiveText().deserialized().bindJson()
    val deviceToken = body["deviceToken"].asString().bindJson()
    val ttpNumber = body["ttpNumber"].asString().bindJson()
    removeAbonoSubscription(AbonoSubscription(ttpNumber, deviceToken.toDeviceToken()))
    ResponseRaw(HttpStatusCode.OK)
}

suspend fun Pipeline.subscriptionAbono() = either {
    val body = call.receiveText().deserialized().bindJson()
    val deviceToken = body["deviceToken"].asString().bindJson()
    val ttpNumber = body["ttpNumber"].asString().bindJson()
    val subscription =
        getAbonoSubscription(AbonoSubscription(ttpNumber, deviceToken.toDeviceToken())) ?: shift<Nothing>(NotFound())
    ResponseJson(buildAbonoSubscriptionJson(subscription), HttpStatusCode.OK)
}