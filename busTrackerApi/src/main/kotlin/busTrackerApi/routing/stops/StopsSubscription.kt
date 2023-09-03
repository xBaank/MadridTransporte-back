package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.await
import busTrackerApi.extensions.forEachAsync
import busTrackerApi.extensions.mapAsync
import busTrackerApi.utils.hourFormatter
import busTrackerApi.utils.timeZoneMadrid
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.*
import java.time.Instant
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes

data class StopsSubscription(
    val deviceToken: String,
    val stopCode: String,
    val codMode: String,
    val lines: List<String>,
    val f: suspend (String) -> Either<BusTrackerException, StopTimes>
)

val subscribedDevices = Cache.Builder()
    .build<String, StopsSubscription>()

fun unsubscribeLineDevice(deviceToken: String, line: String, stopId: String) {
    val tagId = deviceToken + stopId
    val subscribed = subscribedDevices.get(tagId)
    if (subscribed != null) {
        subscribedDevices.put(
            tagId,
            subscribed.copy(lines = subscribed.lines - line)
        )
    } else {
        subscribedDevices.invalidate(tagId)
    }
}

fun unsubscribeAllDevice(deviceToken: String) {
    subscribedDevices.asMap().keys.filter { it.toString().contains(deviceToken) }
        .forEach { subscribedDevices.invalidate(it.toString()) }
}

fun getSubscriptionsByStopCode(deviceToken: String, stopCode: String) =
    subscribedDevices.get(deviceToken + stopCode)

suspend fun subscribeLineDevice(
    deviceToken: String,
    stopId: String,
    line: String,
    codMode: String,
    f: suspend (String) -> Either<BusTrackerException, StopTimes>
) =
    either {
        if (subscribedDevices.asMap().keys.count { it.toString().contains(deviceToken) } >= 5)
            shift<Unit>(BusTrackerException.Unauthorized("Max stops reached"))

        val tagId = deviceToken + stopId
        val subscribed = subscribedDevices.get(tagId)
        if (subscribed?.lines?.contains(line) == true) return@either
        if (subscribed != null) {
            subscribedDevices.put(
                tagId,
                subscribed.copy(lines = subscribed.lines + line)
            )
        } else {
            subscribedDevices.put(
                tagId,
                StopsSubscription(deviceToken, stopId, codMode, listOf(line), f)
            )
        }
    }

fun notifyStopTimesOnBackground() {
    val coroutine = CoroutineScope(Dispatchers.IO)
    coroutine.launch {
        while (isActive) {
            try {
                val stopCodesTimes = subscribedDevices.asMap().values.mapAsync { it to it.f(it.stopCode) }
                subscribedDevices.asMap().forEachAsync { (tagId, stopSub) ->
                    val stopTimesPair = stopCodesTimes.find { it.await().first.stopCode == stopSub.stopCode }?.await()
                    val stopTimes = stopTimesPair?.second?.getOrNull() ?: return@forEachAsync

                    val messages = stopTimes
                        .arrives
                        .distinctBy { Pair(it.line, it.destination) }
                        .mapNotNull {
                            if (!stopSub.lines.contains(it.line)) return@mapNotNull null
                            val timeFormatted = LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(it.estimatedArrive), timeZoneMadrid.toZoneId())
                                .format(hourFormatter)
                            Message.builder()
                                .setWebpushConfig(
                                    WebpushConfig.builder()
                                        .setNotification(
                                            WebpushNotification.builder()
                                                .setTitle("Tiempos de parada ${stopTimes.stopName}")
                                                .setBody("Línea ${it.line} - $timeFormatted - ${it.destination}")
                                                .setRenotify(true)
                                                .setTag(tagId.toString() + it.line)
                                                .build()
                                        )
                                        .build()
                                )
                                .setToken(stopSub.deviceToken)
                                .build()
                        }
                    messages.forEachAsync { FirebaseMessaging.getInstance().sendAsync(it).await() }
                }
            } catch (e: Exception) {
                println("Error notifying stop times: ${e.message}")
            }
            delay(1.minutes)
        }
    }
}


