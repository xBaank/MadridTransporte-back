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
import com.google.cloud.firestore.Filter
import com.google.firebase.ErrorCode.INVALID_ARGUMENT
import com.google.firebase.ErrorCode.NOT_FOUND
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import simpleJson.serialized
import kotlin.time.Duration.Companion.minutes

private typealias StopTimesF = suspend (String) -> Either<BusTrackerException, StopTimes>

private val LOGGER = KtorSimpleLogger("Subscriptions")

data class LineDestination(val line: String = "", val destination: String = "", val codMode: Int = 0)
data class StopsSubscription(
    val deviceTokens: MutableList<String> = mutableListOf(),
    val linesByDeviceToken: MutableMap<String, List<LineDestination>> = mutableMapOf(),
    val stopCode: String = "",
    val codMode: String = "",
    val stopName: String = ""
)

val mutex = Mutex()
private val collection by lazy { FirestoreClient.getFirestore().collection("subscribers") }


private suspend fun getFunctionByCodMode(codMode: String): Either<BusTrackerException, StopTimesF> = either {
    when (codMode) {
        metroCodMode -> {
            { getMetroTimesResponse(it, codMode) }
        }

        busCodMode -> {
            { getBusTimesResponse(it) }
        }

        emtCodMode -> {
            { getEmtStopTimesResponse(it) }
        }

        else -> {
            shift<Nothing>(BusTrackerException.NotFound("CodMode $codMode is not supported"))
        }
    }
}

suspend fun unsubscribeDevice(deviceToken: String, stopCode: String, lineDestination: LineDestination) {
    collection.where(Filter.arrayContains("deviceTokens", deviceToken)).whereEqualTo("stopCode", stopCode).get().await()
        .documents.forEachAsync {
            val subscription = it.toObject(StopsSubscription::class.java)
            subscription.linesByDeviceToken[deviceToken] = subscription.linesByDeviceToken[deviceToken]?.filter {
                it != lineDestination
            } ?: emptyList()
            if (subscription.linesByDeviceToken[deviceToken].isNullOrEmpty()) it.reference.delete().await()
            else it.reference.set(subscription).await()
        }
}

suspend fun unsubscribeAllDevice(deviceToken: String) {
    collection.where(Filter.arrayContains("deviceTokens", deviceToken)).get().await().documents.forEachAsync {
        val subscription = it.toObject(StopsSubscription::class.java)
        subscription.deviceTokens -= deviceToken
        if (subscription.deviceTokens.isEmpty()) it.reference.delete().await()
        else it.reference.set(subscription).await()
    }
}

suspend fun getSubscriptions(deviceToken: String) = collection
    .whereArrayContains("deviceTokens", deviceToken)
    .get().await()
    .documents
    .map { it.toObject(StopsSubscription::class.java) }

suspend fun getSubscription(deviceToken: String, stopCode: String) = either {
    collection
        .whereArrayContains("deviceTokens", deviceToken)
        .whereEqualTo("stopCode", stopCode)
        .get().await()
        .documents
        .map { it.toObject(StopsSubscription::class.java) }
        .firstOrNull()
        ?: shift(BusTrackerException.NotFound("Subscription not found"))
}


suspend fun subscribeDevice(
    deviceToken: String,
    stopId: String,
    lineDestination: LineDestination,
    codMode: String
) = either {
    mutex.withLock {
        val subscription = collection
            .whereEqualTo("stopCode", stopId)
            .whereEqualTo("codMode", codMode)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(StopsSubscription::class.java)
        if (subscription != null
            &&
            deviceToken in subscription.deviceTokens
            &&
            subscription.linesByDeviceToken[deviceToken]?.contains(lineDestination) == true
        ) shift<Nothing>(BusTrackerException.Conflict("Device already subscribed"))
        if (subscription != null) {
            if (deviceToken !in subscription.deviceTokens) subscription.deviceTokens += deviceToken
            subscription.linesByDeviceToken[deviceToken] =
                subscription.linesByDeviceToken[deviceToken]?.plus(lineDestination) ?: listOf(lineDestination)
            collection.document(subscription.stopCode).set(subscription).await()
        }
        if (subscription == null) {
            val newSubscription = StopsSubscription(
                deviceTokens = mutableListOf(deviceToken),
                linesByDeviceToken = mutableMapOf(deviceToken to listOf(lineDestination)),
                stopCode = stopId,
                codMode = codMode,
                stopName = getStopNameByStopCode(stopId).bind()
            )
            collection.document(newSubscription.stopCode).set(newSubscription).await()
        }
    }
}

fun notifyStopTimesOnBackground() {
    val coroutine = CoroutineScope(Dispatchers.Default)
    coroutine.launch {
        while (isActive) {
            collection.get().await().documents.forEachAsync { document ->
                val subscription = document.toObject(StopsSubscription::class.java)
                try {
                    val function = getFunctionByCodMode(subscription.codMode).getOrNull() ?: return@forEachAsync
                    val stopTimes = function(subscription.stopCode).getOrNull() ?: return@forEachAsync
                    subscription.deviceTokens.forEachAsync {
                        val selectedTimes = stopTimes.copy(
                            arrives = stopTimes.arrives.filter { arrive ->
                                subscription.linesByDeviceToken[it]?.any { lineDestination ->
                                    lineDestination.line == arrive.line && lineDestination.destination == arrive.destination
                                } == true
                            }
                        )
                        val message = Message.builder()
                            .putData("stopTimes", buildStopTimesJson(selectedTimes).serialized())
                            .setToken(it)
                            .build()

                        try {
                            LOGGER.info("Sending message to $it")
                            FirebaseMessaging.getInstance().sendAsync(message).await()
                        } catch (e: FirebaseMessagingException) {
                            LOGGER.error(e)
                            if (e.errorCode == INVALID_ARGUMENT || e.errorCode == NOT_FOUND || e.messagingErrorCode == UNREGISTERED) {
                                unsubscribeAllDevice(it)
                            }
                        }
                    }
                } catch (e: Exception) {
                    LOGGER.error(e)
                }
            }
            delay(1.minutes)
        }
    }
}