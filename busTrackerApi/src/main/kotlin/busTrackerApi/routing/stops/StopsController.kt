package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.toBusTrackerException
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import io.github.reactivecircus.cache4k.Cache
import io.ktor.server.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Request
import simpleJson.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

val stopTimesCache = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, TimedCachedValue<ShortStopTimesResponse>>()

val allStopsCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, JsonNode>()

val subscribedStops = mutableMapOf<String, WebSocketServerSession>()

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
    val response = defaultClient.getShortStopTimes(request)
    val alerts = defaultClient.getIncidentsAffectations(IncidentsAffectationsRequest().apply {
        this.codMode = codMode ?: ""
        codLines = ArrayOfString().apply { response.stopTimes.linesStatus.lineStatus.map { it.line.codLine } }
        authentication = defaultClient.auth()
    })
    response
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

suspend fun getAllStopsResponse() = either {
    val cached = allStopsCache.get("all")
    if (cached != null) return@either cached

    val request = Request.Builder().url("https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/Stops.json").get().build()
    httpClient.newCall(request).execute().use {
        val json = it.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().toBusTrackerException().bind()
    }.also { allStopsCache.put("all", it) }
}

suspend fun getStopById(stopCode: String) = either {
    getAllStopsResponse().bind().asArray().toBusTrackerException().bind()
        .firstOrNull { it["codStop"].asString().getOrNull() == stopCode } ?:
    shift<Nothing>(BusTrackerException.NotFound("Stop with id $stopCode not found"))
}

