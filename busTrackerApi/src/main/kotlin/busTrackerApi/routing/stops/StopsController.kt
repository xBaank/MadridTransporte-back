package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.ArrayOfString
import crtm.soap.IncidentsAffectationsRequest
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesRequest
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
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

val allStopsCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, JsonNode>()

val cachedAlerts = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, TimedCachedValue<IncidentsAffectationsResponse>>()

const val allStopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/Stops.json"

private fun getStopTimesResponse(stopCode: String, codMode: String?) = Either.catch {
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

suspend fun getTimesResponse(stopCode: String, codMode: String?) = either {
    val stopTimes = withTimeoutOrNull(20.seconds) {
        CoroutineScope(Dispatchers.IO)
            .async { getStopTimesResponse(stopCode, codMode) }
            .await()
            .bind()
    }

    if(stopTimes == null) shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))

    val result = parseStopTimesResponseToStopTimes(stopTimes).let(::buildJson).timed()
    stopTimesCache.put(stopCode, result)
    result
}

suspend fun getTimesResponseCached(stopCode: String, codMode : String) = either {
    stopTimesCache.get(stopCode) ?: shift<Nothing>(NotFound("Stop with id $stopCode not found"))
}

suspend fun getAllStopsResponse() = either {
    val cached = allStopsCache.get(allStopsUrl)
    if (cached != null) return@either cached

    val response = httpClient.get(allStopsUrl).await().use { response ->
        val json =
            response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().asArray().bindMap()
            .distinctBy { it["IDESTACION"].asString().bindMap() to it["DENOMINACION"].asString().bindMap() }
            .asJson()
    }

    allStopsCache.put(allStopsUrl, response)
    response
}

suspend fun getStopById(stopCode: String) = either {
    getAllStopsResponse().bind().asArray().bindMap()
        .firstOrNull { it["IDESTACION"].asString().getOrNull() == stopCode } ?:
    shift<Nothing>(NotFound("Stop with id $stopCode not found"))
}

suspend fun getAlertsByCodModeResponse(codMode: String) = Either.catch {
    val cached = cachedAlerts.get(codMode)
    if (cached != null) return@catch cached

    val request = IncidentsAffectationsRequest().apply {
        this.codMode = codMode
        codLines = ArrayOfString()
        authentication = defaultClient.auth()
    }

    val result = withTimeoutOrNull(20.seconds) {
        CoroutineScope(Dispatchers.IO)
            .async { defaultClient.getIncidentsAffectations(request).timed() }
            .await()
    }

    if(result == null) throw BusTrackerException.InternalServerError("Got empty response")

    cachedAlerts.put(codMode, result)
    result
}.mapLeft(mapExceptionsF)

