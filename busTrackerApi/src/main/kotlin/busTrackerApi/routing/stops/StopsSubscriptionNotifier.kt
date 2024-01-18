package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.EnvVariables
import busTrackerApi.db.getSubscriptions
import busTrackerApi.db.unsubscribeAllDevice
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.await
import busTrackerApi.extensions.batched
import busTrackerApi.extensions.forEachAsync
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.routing.stops.emt.emtCodMode
import busTrackerApi.routing.stops.emt.getEmtStopTimes
import busTrackerApi.routing.stops.metro.getMetroTimesResponse
import busTrackerApi.routing.stops.metro.metroCodMode
import busTrackerApi.routing.stops.metro.tramCodMode
import busTrackerApi.routing.stops.train.getTrainTimes
import busTrackerApi.routing.stops.trainRouted.trainCodMode
import busTrackerApi.utils.StopTimesF
import com.google.firebase.ErrorCode.INVALID_ARGUMENT
import com.google.firebase.ErrorCode.NOT_FOUND
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import simpleJson.serialized


private val LOGGER = KtorSimpleLogger("Subscriptions")

suspend fun getFunctionByCodMode(codMode: String): Either<BusTrackerException, StopTimesF> = either {
    when (codMode) {
        metroCodMode -> {
            { getMetroTimesResponse(it, codMode) }
        }

        tramCodMode -> {
            { getMetroTimesResponse(it, codMode) }
        }

        busCodMode -> {
            { getBusTimesResponse(it) }
        }

        emtCodMode -> {
            { getEmtStopTimes(it) }
        }

        trainCodMode -> {
            { getTrainTimes(it) }
        }

        else -> {
            shift<Nothing>(BusTrackerException.NotFound("CodMode $codMode is not supported"))
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun notifyStopTimesOnBackground() {
    GlobalScope.launch(Dispatchers.IO) {
        while (isActive) {
            getSubscriptions().batched(100).forEachAsync { subscription ->
                try {
                    val function = getFunctionByCodMode(subscription.codMode).getOrNull() ?: return@forEachAsync
                    val stopTimes = function(subscription.stopCode).getOrNull() ?: return@forEachAsync

                    subscription.deviceTokens.distinctBy { it.token }.forEachAsync {
                        val selectedTimes = stopTimes.copy(
                            arrives = stopTimes.arrives?.filter { arrive ->
                                subscription.linesByDeviceToken[it.token]?.any { lineDestination ->
                                    lineDestination.line == arrive.line && lineDestination.destination == arrive.destination
                                } == true
                            } ?: emptyList()
                        )

                        val message = Message.builder()
                            .putData("stopTimes", buildStopTimesJson(selectedTimes).serialized())
                            .setToken(it.token)
                            .build()

                        try {
                            LOGGER.info("Sending message to $it")
                            FirebaseMessaging.getInstance().sendAsync(message).await()
                        } catch (e: FirebaseMessagingException) {
                            LOGGER.error(e)
                            if (e.errorCode == INVALID_ARGUMENT || e.errorCode == NOT_FOUND || e.messagingErrorCode == UNREGISTERED) {
                                unsubscribeAllDevice(it)
                            }
                        } catch (e: Exception) {
                            LOGGER.error(e)
                        }
                    }
                } catch (e: Exception) {
                    LOGGER.error(e)
                }
            }
            delay(EnvVariables.notificationDelayTimeSeconds)
        }
    }
}