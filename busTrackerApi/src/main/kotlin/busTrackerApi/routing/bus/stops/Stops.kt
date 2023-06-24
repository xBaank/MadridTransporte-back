package busTrackerApi.routing.bus.stops

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import crtm.auth
import crtm.defaultClient
import crtm.soap.ShortStopTimesRequest
import crtm.soap.ShortStopTimesResponse
import crtm.soap.ShortTime
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.jObject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

val stopTimesCache = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, TimedCachedValue<JsonNode>>()

fun getStopTimes(stopCode: String, codMode: String?) = Either.catch {
    val request = ShortStopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.auth()
    }
    if (codMode != null) request.codMode = codMode
    defaultClient.getShortStopTimes(request)
}

suspend fun tryGetTimes(stopCode: String, codMode: String?) =
    tryGetTimes(stopCode, codMode, ::getStopTimes)

suspend fun tryGetTimesOrCached(stopCode: String, codMode: String?) =
    tryGetTimes(stopCode, codMode).getOrNull()
        ?.also { stopTimesCache.put(stopCode, it) }
        ?: stopTimesCache.get(stopCode)


private suspend inline fun <T> tryGetTimes(
    stopCode: String,
    codMode: String?,
    crossinline getF: (stopCode: String, codMode: String?) -> Either<T, ShortStopTimesResponse>
) = withTimeoutOrNull(20.seconds) {
    val stopTimes = CoroutineScope(Dispatchers.IO)
        .async { getF(stopCode, codMode) }
        .await()
        .getOrNull()

    stopTimes?.stopTimes?.times?.shortTime?.map(::buildStopTimesJson)?.asJson()?.timed()
}?.right() ?: Exception("No stop times found for stop code $stopCode").left()

fun buildStopTimesJson(time: ShortTime) = jObject {
    "lineCode" += time.codLine
    "destination" += time.destination
    "codVehicle" += time.codVehicle
    "time" += time.time.toGregorianCalendar().time.toInstant().toEpochMilli()
}