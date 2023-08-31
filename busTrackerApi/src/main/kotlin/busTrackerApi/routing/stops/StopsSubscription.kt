package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.getOrElse
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.await
import busTrackerApi.extensions.forEachAsync
import busTrackerApi.utils.hourFormatter
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class StopsSubscription(val deviceToken: String, val f: suspend () -> Either<BusTrackerException, StopTimes>)

val subscribedDevices = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, StopsSubscription>()

fun unsubscribeDevice(deviceToken: String, stopId: String) {
    val tagId = deviceToken + stopId
    subscribedDevices.invalidate(tagId)
}

fun isSubscribed(deviceToken: String, stopId: String) = subscribedDevices.get(deviceToken + stopId) != null

suspend fun subscribeDevice(
    deviceToken: String,
    stopId: String,
    f: suspend () -> Either<BusTrackerException, StopTimes>
) =
    either {
        val tagId = deviceToken + stopId
        if (subscribedDevices.asMap().keys.count { it.toString().contains(deviceToken) } >= 5)
            shift<Unit>(BusTrackerException.BadRequest("Max stops reached"))
        subscribedDevices.put(tagId, StopsSubscription(deviceToken, f))
    }

fun notifyStopTimesOnBackground() {
    val coroutine = CoroutineScope(Dispatchers.IO)
    coroutine.launch {
        while (isActive) {
            subscribedDevices.asMap().forEachAsync { (tagId, stopSub) ->
                val stopTimes = stopSub.f().getOrElse { return@forEachAsync } //TODO Notify error?
                val timesFormatted = stopTimes.arrives.joinToString("\n")
                {
                    "${it.line} - ${
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(it.estimatedArrive), ZoneOffset.UTC).format(
                            hourFormatter
                        )
                    }"
                }
                val message: Message = Message.builder()
                    .setWebpushConfig(
                        WebpushConfig.builder()
                            .setNotification(
                                WebpushNotification.builder()
                                    .setTitle("Tiempos de parada ${stopTimes.stopName}")
                                    .setBody(timesFormatted)
                                    .setRenotify(true)
                                    .setTag(tagId.toString())
                                    .build()
                            )
                            .build()
                    )
                    .setToken(stopSub.deviceToken)
                    .build()
                FirebaseMessaging.getInstance().sendAsync(message).await()
            }
            delay(1.minutes)
        }
    }
}


