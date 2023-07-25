package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import crtm.utils.getCodModeFromLineCode
import crtm.utils.getCodStopFromStopCode
import io.github.reactivecircus.cache4k.Cache
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import simpleJson.jArray
import simpleJson.jObject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

val stopTimesCache = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, TimedCachedValue<ShortStopTimesResponse>>()

val subscribedStops = mutableMapOf<String, WebSocketServerSession>()


fun getStopsByQueryResponse(query: String) = Either.catch {
    val request = StopRequestV2().apply {
        customSearch = query
        authentication = defaultClient.auth()
    }
    defaultClient.getStopsV2(request)
}.mapLeft(mapExceptionsF)

fun getStopTimesResponse(stopCode: String, codMode: String?) = Either.catch {
    val request = ShortStopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.auth()
    }
    //Not needed?
    if (codMode != null) request.codMode = codMode
    defaultClient.getShortStopTimes(request)
}.mapLeft(mapExceptionsF)

suspend fun getTimesResponse(stopCode: String, codMode: String?) =
    getTimes(stopCode, codMode, ::getStopTimesResponse)
        .mapLeft(mapExceptionsF)

suspend fun getTimesOrCachedResponse(stopCode: String, codMode: String?) =
    getTimesResponse(stopCode, codMode)
        .onRight { stopTimesCache.put(stopCode, it) }
        .getOrElse { stopTimesCache.get(stopCode) }?.right() ?:
    BusTrackerException.NotFound("No stop times found for stop code $stopCode").left()



private suspend inline fun <T> getTimes(
    stopCode: String,
    codMode: String?,
    crossinline getF: (stopCode: String, codMode: String?) -> Either<T, ShortStopTimesResponse>
) = withTimeoutOrNull(20.seconds) {
    val stopTimes = CoroutineScope(Dispatchers.IO)
        .async { getF(stopCode, codMode) }
        .await()
        .getOrNull()

    stopTimes?.timed()
}?.right() ?: BusTrackerException.NotFound("No stop times found for stop code $stopCode").left()

fun getStopsByLocationResponse(lat: Double, lon: Double) = Either.catch {
    val request = StopsByGeoLocationRequest().apply {
        coordinates = Coordinates().apply {
            latitude = lat
            longitude = lon
        }
        method = 1
        precision = 1000
        codMode = ""
        authentication = defaultClient.auth()
    }
    defaultClient.getNearestStopsByGeoLocation(request)
}.mapLeft(mapExceptionsF)

fun buildStopsJson(stops: StopResponse) = jArray {
    stops.stops.stop.forEach { stop ->
        addObject {
            "codStop" += stop.codStop
            "simpleCodStop" += getCodStopFromStopCode(stop.codStop)
            "codMode" += stop.codMode
            "name" += stop.name
            "latitude" += stop.coordinates.latitude
            "longitude" += stop.coordinates.longitude
        }
    }
}

fun buildStopLocationsJson(stops: StopsByGeoLocationResponse) = jArray {
    stops.stops.stop.forEach { stop ->
        addObject {
            "codStop" += stop.codStop
            "simpleCodStop" += getCodStopFromStopCode(stop.codStop)
            "codMode" += stop.codMode
            "name" += stop.name
            "latitude" += stop.coordinates.latitude
            "longitude" += stop.coordinates.longitude
        }
    }
}

fun buildStopTimesJson(times: ShortStopTimesResponse) = jObject {
    "name" += times.stopTimes.stop.name
    "times" += jArray {
        times.stopTimes?.times?.shortTime?.forEach {
            addObject {
                "lineCode" += it.codLine
                "codMode" += getCodModeFromLineCode(it.codLine)
                "destination" += it.destination
                "codVehicle" += it.codVehicle
                "time" += it.time.toGregorianCalendar().time.toInstant().toEpochMilli()
            }
        }
    }
}