package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.asNumberOrString
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.*
import crtm.soap.ArrayOfString
import crtm.soap.IncidentsAffectationsRequest
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesRequest
import crtm.utils.getCodStopFromStopCode
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.withTimeoutOrNull
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import kotlin.time.Duration.Companion.hours

val allStopsCache = Cache.Builder()
    .build<String, JsonNode>()

val cachedAlerts = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, IncidentsAffectationsResponse>()

private suspend fun getStopTimesResponse(stopCode: String) = Either.catch {
    val request = StopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.value().auth()
    }
    getSuspend(request, defaultClient.value()::getStopTimesAsync)
}.mapLeft(mapExceptionsF)

suspend fun getBusTimesResponse(stopCode: String) = either {
    val stopTimes = withTimeoutOrNull(timeoutSeconds) {
        getStopTimesResponse(stopCode).getOrNull()
    }

    val result = parseStopTimesResponseToStopTimes(
        stopTimes,
        getCoordinatesByStopCode(stopCode).bind(),
        getStopNameByStopCode(stopCode).getOrNull(),
        getCodStopFromStopCode(stopCode)
    )

    result
}

suspend fun getAllStopsInfoResponse() = either {
    val cached = allStopsCache.get(allStopsInfoUrl)
    if (cached != null) return@either cached

    val response = httpClient.get(allStopsInfoUrl).await().use { response ->
        val json =
            response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().asArray().bindMap().asJson()
    }

    allStopsCache.put(allStopsInfoUrl, response)
    response
}

suspend fun getAllStopsResponse() = either {
    val cached = allStopsCache.get(allStopsUrl)
    if (cached != null) return@either cached

    val response = httpClient.get(allStopsUrl).await().use { response ->
        val json =
            response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().asArray().bindMap()
    }

    allStopsCache.put(allStopsUrl, response)
    response
}

suspend fun getIdByStopCode(stopCode: String) = either {
    getAllStopsInfoResponse().bind().asArray().bindMap()
        .firstOrNull { it["IDESTACION"].asString().getOrNull() == stopCode }
        ?.get("CODIGOEMPRESA")?.asNumberOrString()
        ?: shift<Nothing>(NotFound("Stop with stopCode $stopCode not found"))
}

suspend fun getStopCodeById(id: String) = either {
    getAllStopsInfoResponse().bind().asArray().bindMap()
        .firstOrNull { it["CODIGOEMPRESA"].asNumberOrString() == id }
        ?.get("IDESTACION")?.asString()?.bindMap()
        ?: shift<Nothing>(NotFound("Stop with id $id not found"))
}

suspend fun getStopNameById(id: String) = either {
    val stopCode = getAllStopsInfoResponse().bind().asArray().bindMap()
        .firstOrNull { it["CODIGOEMPRESA"].asNumberOrString() == id }
        ?.get("IDESTACION")?.asNumberOrString()
        ?: shift<Nothing>(NotFound("Stop with id $id not found"))
    getStopNameByStopCode(stopCode).bind()
}

suspend fun getCoordinatesByStopCode(id: String): Either<BusTrackerException, Coordinates> = either {
    getAllStopsResponse().bind().asArray().bindMap()
        .firstOrNull { it["full_stop_code"].asNumberOrString() == id }
        ?.let {
            Coordinates(
                it["stop_lat"].asNumberOrString().toDouble(),
                it["stop_lon"].asNumberOrString().toDouble()
            )
        }
        ?: shift<Nothing>(NotFound("Stop with id $id not found"))
}

suspend fun getStopNameByStopCode(id: String) = either {
    getAllStopsResponse().bind().asArray().bindMap()
        .firstOrNull { it["full_stop_code"].asString().getOrNull() == id }
        ?.get("stop_name")?.asString()?.bindMap()
        ?: shift<Nothing>(NotFound("Stop with id $id not found"))
}

suspend fun checkStopExists(stopCode: String) = either {
    getAllStopsResponse().bind().asArray().bindMap()
        .firstOrNull { it["full_stop_code"].asString().getOrNull() == stopCode }
        ?: shift<Nothing>(NotFound("Stop with id $stopCode not found"))
}

suspend fun getAlertsByCodModeResponse(codMode: String) = Either.catch {
    val cached = cachedAlerts.get(codMode)
    if (cached != null) return@catch cached

    val result = withTimeoutOrNull(timeoutSeconds) {
        val request = IncidentsAffectationsRequest().apply {
            this.codMode = codMode
            codLines = ArrayOfString()
            authentication = defaultClient.value().auth()
        }

        getSuspend(request, defaultClient.value()::getIncidentsAffectationsAsync)
    }

    if (result == null) throw BusTrackerException.InternalServerError("Got empty response")

    cachedAlerts.put(codMode, result)
    result
}.mapLeft(mapExceptionsF)

