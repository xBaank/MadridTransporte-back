package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.await
import busTrackerApi.extensions.forEachAsync
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.routing.stops.emt.emtCodMode
import busTrackerApi.routing.stops.emt.getEmtStopTimesResponse
import busTrackerApi.routing.stops.metro.getMetroTimesResponse
import busTrackerApi.routing.stops.metro.metroCodMode
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import simpleJson.serialized
import kotlin.time.Duration.Companion.minutes

private typealias StopTimesF = suspend (String) -> Either<BusTrackerException, StopTimes>

data class StopsSubscription(
    val deviceTokens: MutableList<String>,
    val stopCode: String,
    val codMode: String
)

val mutex = Mutex()
val subscribedDevices = mutableListOf<StopsSubscription>()

private suspend fun getFunctionByCodMode(codMode: String): Either<BusTrackerException, StopTimesF> = either {
    when (codMode) {
        metroCodMode -> {
            { getMetroTimesResponse(it, metroCodMode).map(TimedCachedValue<StopTimes>::value) }
        }

        busCodMode -> {
            { getBusTimesResponse(it).map(TimedCachedValue<StopTimes>::value) }
        }

        emtCodMode -> {
            { getEmtStopTimesResponse(it).map(TimedCachedValue<StopTimes>::value) }
        }

        else -> {
            shift<Nothing>(BusTrackerException.NotFound("CodMode $codMode is not supported"))
        }
    }
}

suspend fun unsubscribeDevice(deviceToken: String, stopCode: String) =
    subscribedDevices.filter { it.deviceTokens.contains(deviceToken) && it.stopCode == stopCode }
        .forEachAsync { it.deviceTokens -= deviceToken }

suspend fun unsubscribeAllDevice(deviceToken: String) {
    subscribedDevices.forEachAsync { subscription ->
        if (deviceToken in subscription.deviceTokens)
            subscription.deviceTokens -= deviceToken

    }
}

fun getSubscriptionsByStopCode(deviceToken: String, stopCode: String) =
    subscribedDevices.filter { deviceToken in it.deviceTokens && it.stopCode == stopCode }

suspend fun subscribeDevice(
    deviceToken: String,
    stopId: String,
    codMode: String
) = either {
    val subscription = subscribedDevices.find { it.stopCode == stopId && it.codMode == codMode }
    if (subscription != null && deviceToken in subscription.deviceTokens) shift<Nothing>(BusTrackerException.Conflict("Device already subscribed"))
    if (subscription != null) mutex.withLock { subscription.deviceTokens += deviceToken }
    if (subscription == null) mutex.withLock {
        subscribedDevices += StopsSubscription(
            mutableListOf(deviceToken),
            stopId,
            codMode
        )
    }
}

fun notifyStopTimesOnBackground() {
    val coroutine = CoroutineScope(Dispatchers.IO)
    coroutine.launch {
        while (isActive) {
            subscribedDevices.forEachAsync { subscription ->
                try {
                    val function = getFunctionByCodMode(subscription.codMode).getOrNull() ?: return@forEachAsync
                    val stopTimes = function(subscription.stopCode).getOrNull() ?: return@forEachAsync
                    val messages = subscription.deviceTokens.map {
                        Message.builder()
                            .putData("stopTimes", buildJson(stopTimes).serialized())
                            .setToken(it)
                            .build()
                    }
                    messages.forEachAsync { FirebaseMessaging.getInstance().sendAsync(it).await() }
                } catch (e: Exception) {
                    println(e)
                }
            }
            delay(1.minutes)
        }
    }
}