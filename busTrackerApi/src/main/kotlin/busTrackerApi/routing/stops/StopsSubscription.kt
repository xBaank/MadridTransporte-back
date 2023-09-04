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
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import simpleJson.serialized
import java.util.*
import kotlin.time.Duration.Companion.minutes

private typealias StopTimesF = suspend (String) -> Either<BusTrackerException, StopTimes>

data class StopsSubscription(
    val deviceTokens: MutableList<String> = mutableListOf(),
    val stopCode: String = "",
    val codMode: String = "",
)

val mutex = Mutex()
private val collection by lazy { FirestoreClient.getFirestore().collection("subscribers") }


private suspend fun getFunctionByCodMode(codMode: String): Either<BusTrackerException, StopTimesF> = either {
    when (codMode) {
        metroCodMode -> {
            { getMetroTimesResponse(it, codMode).map(TimedCachedValue<StopTimes>::value) }
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

suspend fun unsubscribeDevice(deviceToken: String, stopCode: String) {
    collection.where(Filter.arrayContains("deviceTokens", deviceToken)).whereEqualTo("stopCode", stopCode).get().await()
        .documents.forEachAsync { it.reference.delete().await() }
}

suspend fun unsubscribeAllDevice(deviceToken: String) {
    collection.where(Filter.arrayContains("deviceTokens", deviceToken)).get().await().documents.forEachAsync {
        it.reference.delete().await()
    }
}

suspend fun getSubscriptionsByStopCode(deviceToken: String, stopCode: String) =
    collection.whereEqualTo("stopCode", stopCode).whereArrayContains("deviceTokens", deviceToken).get().await()
        .documents.map { it.toObject(StopsSubscription::class.java) }

suspend fun subscribeDevice(
    deviceToken: String,
    stopId: String,
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
        if (subscription != null && deviceToken in subscription.deviceTokens) shift<Nothing>(
            BusTrackerException.Conflict(
                "Device already subscribed"
            )
        )
        if (subscription != null) {
            subscription.deviceTokens += deviceToken
            collection.document(subscription.stopCode).set(subscription).await()
        }
        if (subscription == null) {
            val newSubscription = StopsSubscription(
                deviceTokens = mutableListOf(deviceToken),
                stopCode = stopId,
                codMode = codMode
            )
            collection.document(newSubscription.stopCode).set(newSubscription).await()
        }
    }
}

fun notifyStopTimesOnBackground() {
    val coroutine = CoroutineScope(Dispatchers.IO)
    coroutine.launch {
        while (isActive) {
            collection.get().await().documents.forEachAsync {
                val subscription = it.toObject(StopsSubscription::class.java)
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