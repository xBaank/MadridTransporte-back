package api.notifications

import api.config.EnvVariables.notificationDelayTimeSeconds
import api.db.getSubscriptions
import api.db.models.StopsSubscription
import api.db.unsubscribeAllDevice
import api.exceptions.BusTrackerException
import api.exceptions.BusTrackerException.NotFound
import api.extensions.await
import api.extensions.batched
import api.extensions.forEachAsync
import api.extensions.mapAsync
import api.routing.stops.bus.busCodMode
import api.routing.stops.bus.getCRTMStopTimes
import api.routing.stops.emt.emtCodMode
import api.routing.stops.emt.getEmtStopTimes
import api.routing.stops.metro.getMetroTimes
import api.routing.stops.metro.metroCodMode
import api.routing.stops.metro.tramCodMode
import api.routing.stops.train.getTrainTimes
import api.routing.stops.trainRouted.trainCodMode
import api.utils.StopTimesF
import arrow.core.Either
import arrow.core.raise.either
import com.google.firebase.ErrorCode.INVALID_ARGUMENT
import com.google.firebase.ErrorCode.NOT_FOUND
import com.google.firebase.messaging.*
import com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED
import dev.inmo.krontab.builder.buildSchedule
import dev.inmo.krontab.doInfinityTz
import io.ktor.util.logging.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds


private val logger = KtorSimpleLogger("Subscriptions")

suspend fun getFunctionByCodMode(codMode: String): Either<BusTrackerException, StopTimesF> = either {
    when (codMode) {
        metroCodMode -> {
            { getMetroTimes(it) }
        }

        tramCodMode -> {
            { getCRTMStopTimes(it) }
        }

        busCodMode -> {
            { getCRTMStopTimes(it) }
        }

        emtCodMode -> {
            { getEmtStopTimes(it) }
        }

        trainCodMode -> {
            { getTrainTimes(it) }
        }

        else -> {
            raise(NotFound("CodMode $codMode is not supported"))
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun notifyStopTimesOnBackground() = GlobalScope.launch(Dispatchers.IO) {
    val scheduler = buildSchedule {
        seconds {
            every(notificationDelayTimeSeconds.inWholeSeconds.toInt())
        }
    }

    scheduler.doInfinityTz {
        try {
            sendStopTimesNotifications()
        } catch (e: Exception) {
            logger.error(e)
        }
    }
}

suspend fun sendStopTimesNotifications() = getSubscriptions().batched(100).forEachAsync(::sendNotification)

private suspend fun sendNotification(subscription: StopsSubscription) {
    val function = getFunctionByCodMode(subscription.codMode).onLeft(logger::error).getOrNull() ?: return
    val stopTimes = function(subscription.stopCode).getOrNull() ?: return

    subscription.deviceTokens.distinctBy { it.token }.mapAsync {
        val selectedTimes = stopTimes.copy(
            arrives = stopTimes.arrives?.filter { arrive ->
                subscription.linesByDeviceToken[it.token]?.any { lineDestination ->
                    lineDestination.line.equals(
                        arrive.line,
                        true
                    ) && lineDestination.destination.equals(
                        arrive.destination,
                        true
                    )
                } == true
            } ?: emptyList()
        )

        val arrivesToNotify = selectedTimes.arrives?.sortedBy { it.estimatedArrive }
            ?.distinctBy { it.line to it.destination }

        arrivesToNotify?.forEach { arrive ->
            val diff = arrive.estimatedArrive - Instant.now(Clock.systemUTC()).toEpochMilli()
            val time = diff.milliseconds.inWholeMinutes
            val icon = "https://www.madridtransporte.com/favicon.ico"
            val tag = stopTimes.stopCode + arrive.line + arrive.destination
            val title = "Parada ${stopTimes.stopName} - ${arrive.line} - ${arrive.destination}"
            val body = "$time minutos"

            val message = Message.builder()
                .setAndroidConfig(
                    AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(
                            AndroidNotification.builder()
                                .setIcon(icon)
                                .setTag(tag)
                                .setTitle(title)
                                .setBody(body)
                                .build()
                        ).build()
                )
                .setWebpushConfig(
                    WebpushConfig.builder()
                        .putHeader("Urgency", "high")
                        .setNotification(
                            WebpushNotification.builder()
                                .setIcon(icon)
                                .setTag(tag)
                                .setTitle(title)
                                .setBody(body)
                                .setRenotify(true)
                                .build()
                        ).build()
                )
                .setToken(it.token)
                .build()

            try {
                logger.info("Sending stopTimes notification message to $it")
                FirebaseMessaging.getInstance().sendAsync(message).await()
            } catch (e: FirebaseMessagingException) {
                logger.error(e)
                if (e.errorCode == INVALID_ARGUMENT || e.errorCode == NOT_FOUND || e.messagingErrorCode == UNREGISTERED) {
                    unsubscribeAllDevice(it)
                }
            } catch (e: Exception) {
                logger.error(e)
            }
        }
    }
}