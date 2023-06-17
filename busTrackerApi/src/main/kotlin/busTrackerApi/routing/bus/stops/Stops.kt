package busTrackerApi.routing.bus.stops

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.TimedCachedValue
import busTrackerApi.timed
import crtm.auth
import crtm.defaultClient
import crtm.soap.ShortStopTimesRequest
import crtm.soap.ShortTime
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
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

suspend fun tryGetTimes(stopCode: String, codMode: String?) = withTimeout(20.seconds) {
    val stopTimes = CoroutineScope(Dispatchers.IO)
        .async { getStopTimes(stopCode, codMode) }
        .await()
        .getOrElse { null }

    stopTimes?.stopTimes?.times?.shortTime?.map(::buildStopTimesJson)?.asJson()?.timed()
}?.right() ?: Exception("No stop times found for stop code $stopCode").left()

fun buildStopTimesJson(time: ShortTime) = jObject {
    "lineCode" += time.codLine
    "destination" += time.destination
    "codVehicle" += time.codVehicle
    "time" += time.time.toGregorianCalendar().time.toInstant().toEpochMilli()
}