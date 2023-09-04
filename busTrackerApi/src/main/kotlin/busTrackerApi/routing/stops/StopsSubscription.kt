package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.await
import busTrackerApi.extensions.forEachAsync
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import simpleJson.serialized
import kotlin.time.Duration.Companion.minutes

data class StopsSubscription(
    val deviceTokens: MutableList<String>,
    val stopCode: String,
    val codMode: String,
    val f: suspend (String) -> Either<BusTrackerException, StopTimes>
)

val mutex = Mutex()
val subscribedDevices = mutableListOf<StopsSubscription>()

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
    codMode: String,
    f: suspend (String) -> Either<BusTrackerException, StopTimes>
) = either {
    val subscription = subscribedDevices.find { it.stopCode == stopId && it.codMode == codMode }
    if (subscription != null && deviceToken in subscription.deviceTokens) shift<Nothing>(BusTrackerException.Conflict("Device already subscribed"))
    if(subscription != null) mutex.withLock { subscription.deviceTokens += deviceToken }
    if(subscription == null) mutex.withLock { subscribedDevices += StopsSubscription(mutableListOf(deviceToken), stopId, codMode, f) }
}

fun notifyStopTimesOnBackground() {
    val coroutine = CoroutineScope(Dispatchers.IO)
    coroutine.launch {
        while (isActive) {
            subscribedDevices.forEachAsync { subscription ->
                try {
                    val stopTimes = subscription.f(subscription.stopCode).getOrNull() ?: return@forEachAsync
                    val messages = subscription.deviceTokens.map {
                        Message.builder()
                            .putData("stopTimes", buildJson(stopTimes).serialized())
                            .setToken(it)
                            .build()
                    }
                    messages.forEachAsync { FirebaseMessaging.getInstance().sendAsync(it).await() }
                }
                catch (e: Exception) {
                    println(e)
                }
            }
            delay(1.minutes)
        }
    }
}