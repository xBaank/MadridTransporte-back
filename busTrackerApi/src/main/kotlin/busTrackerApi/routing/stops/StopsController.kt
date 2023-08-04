package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

val stopTimesCache = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, TimedCachedValue<StopTimesResponse>>()

val allStopsCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, JsonNode>()

val cachedAlerts = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, TimedCachedValue<IncidentsAffectationsResponse>>()

const val allStopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/Stops.json"

fun getStopTimesResponse(stopCode: String, codMode: String?) = Either.catch {
    val request = StopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.auth()
    }
    //Not needed?
    if (codMode != null) request.codMode = codMode
    defaultClient.getStopTimes(request)
}.mapLeft(mapExceptionsF)

suspend fun getTimesResponse(stopCode: String, codMode: String?) =
    getTimes(stopCode, codMode, ::getStopTimesResponse)
        .mapLeft(mapExceptionsF)


private suspend fun <T> getTimes(
    stopCode: String,
    codMode: String?,
    getF: (stopCode: String, codMode: String?) -> Either<T, StopTimesResponse>
) = withTimeoutOrNull(20.seconds) {
    val stopTimes = CoroutineScope(Dispatchers.IO)
        .async { getF(stopCode, codMode) }
        .await()
        .getOrNull()

    stopTimes?.timed()
}?.right() ?: NotFound("No stop times found for stop code $stopCode").left()

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
    val cached = allStopsCache.get(allStopsUrl)
    if (cached != null) return@either cached

    httpClient.get(allStopsUrl).await().use { response ->
        val json = response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().asArray().bindMap()
            .distinctBy { it["IDESTACION"].asString().bindMap() to it["DENOMINACION"].asString().bindMap() }
            .asJson()
    }.also { allStopsCache.put(allStopsUrl, it) }
}

suspend fun getStopById(stopCode: String) = either {
    getAllStopsResponse().bind().asArray().bindMap()
        .firstOrNull { it["IDESTACION"].asString().getOrNull() == stopCode } ?:
    shift<Nothing>(NotFound("Stop with id $stopCode not found"))
}

suspend fun getAlertsByCodModeResponse(codMode: String) = Either.catch {
    cachedAlerts.get(codMode) ?:
    CoroutineScope(Dispatchers.IO).async {
        val cached = defaultClient.getIncidentsAffectations(IncidentsAffectationsRequest().apply {
            this.codMode = codMode
            codLines = ArrayOfString()
            authentication = defaultClient.auth()
        }).timed()
        cachedAlerts.put(codMode, cached)
        cached
    }.await()

}.mapLeft(mapExceptionsF)

