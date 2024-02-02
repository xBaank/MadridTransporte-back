package busTrackerApi.notifications

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.EnvVariables
import busTrackerApi.db.getSubscriptions
import busTrackerApi.db.models.StopsSubscription
import busTrackerApi.db.unsubscribeAllDevice
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.await
import busTrackerApi.extensions.batched
import busTrackerApi.extensions.forEachAsync
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.routing.stops.bus.getBusStopTimes
import busTrackerApi.routing.stops.emt.emtCodMode
import busTrackerApi.routing.stops.emt.getEmtStopTimes
import busTrackerApi.routing.stops.metro.getMetroTimes
import busTrackerApi.routing.stops.metro.metroCodMode
import busTrackerApi.routing.stops.metro.tramCodMode
import busTrackerApi.routing.stops.train.getTrainStopTimes
import busTrackerApi.routing.stops.trainRouted.trainCodMode
import busTrackerApi.utils.StopTimesF
import com.google.firebase.ErrorCode.INVALID_ARGUMENT
import com.google.firebase.ErrorCode.NOT_FOUND
import com.google.firebase.messaging.*
import com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import java.time.Clock
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds


private val logger = KtorSimpleLogger("Subscriptions")

suspend fun getFunctionByCodMode(codMode: String): Either<BusTrackerException, StopTimesF> = either {
    when (codMode) {
        metroCodMode -> {
            { getMetroTimes(it, codMode) }
        }

        tramCodMode -> {
            { getMetroTimes(it, codMode) }
        }

        busCodMode -> {
            { getBusStopTimes(it) }
        }

        emtCodMode -> {
            { getEmtStopTimes(it) }
        }

        trainCodMode -> {
            { getTrainStopTimes(it) }
        }

        else -> {
            shift<Nothing>(NotFound("CodMode $codMode is not supported"))
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun notifyStopTimesOnBackground() =
    GlobalScope.launch(Dispatchers.IO) {
        while (isActive) {
            try {
                getSubscriptions().batched(100).forEachAsync(::sendNotification)
            }
            catch (e: Exception) {
                logger.error(e)
            }
            delay(EnvVariables.notificationDelayTimeSeconds)
        }
    }

private suspend fun sendNotification(subscription: StopsSubscription) {
    val function = getFunctionByCodMode(subscription.codMode).getOrNull() ?: return
    val stopTimes = function(subscription.stopCode).getOrNull() ?: return

    subscription.deviceTokens.distinctBy { it.token }.forEachAsync {
        val selectedTimes = stopTimes.copy(
            arrives = stopTimes.arrives?.filter { arrive ->
                subscription.linesByDeviceToken[it.token]?.any { lineDestination ->
                    lineDestination.line == arrive.line && lineDestination.destination == arrive.destination
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
                logger.info("Sending message to $it")
                FirebaseMessaging.getInstance().sendAsync(message).await()
            }
            catch (e: FirebaseMessagingException) {
                logger.error(e)
                if (e.errorCode == INVALID_ARGUMENT || e.errorCode == NOT_FOUND || e.messagingErrorCode == UNREGISTERED) {
                    unsubscribeAllDevice(it)
                }
            }
            catch (e: Exception) {
                logger.error(e)
            }
        }
    }
}