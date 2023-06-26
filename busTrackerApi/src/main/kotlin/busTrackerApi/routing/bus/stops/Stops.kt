package busTrackerApi.routing.bus.stops

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import crtm.utils.getCodModeFromLineCode
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.jArray
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

fun getStopsByLocation(lat: Double, lon: Double) = Either.catch {
    val request = StopsByGeoLocationRequest().apply {
        coordinates = Coordinates().apply {
            latitude = lat
            longitude = lon
        }
        method = 1
        precision = 1000
        codMode = "8"
        authentication = defaultClient.auth()
    }
    defaultClient.getNearestStopsByGeoLocation(request)
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
    "codMode" += getCodModeFromLineCode(time.codLine)
    "destination" += time.destination
    "codVehicle" += time.codVehicle
    "time" += time.time.toGregorianCalendar().time.toInstant().toEpochMilli()
}

fun buildStopLocationsJson(stops: StopsByGeoLocationResponse) = jArray {
    stops.stops.stop.forEach { stop ->
        addObject {
            "codStop" += stop.codStop
            "codMode" += stop.codMode
            "name" += stop.name
            "latitude" += stop.coordinates.latitude
            "longitude" += stop.coordinates.longitude
        }
    }
}