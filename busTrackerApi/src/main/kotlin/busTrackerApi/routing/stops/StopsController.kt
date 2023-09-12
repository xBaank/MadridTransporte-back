package busTrackerApi.routing.stops

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.asNumberOrString
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.onEachAsync
import busTrackerApi.routing.stops.metro.metroCodMode
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.ArrayOfString
import crtm.soap.IncidentsAffectationsRequest
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesRequest
import crtm.utils.getCodStopFromStopCode
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import java.util.UUID.randomUUID
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

val allStopsCache = Cache.Builder()
    .build<String, JsonNode>()

val cachedAlerts = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, IncidentsAffectationsResponse>()

const val allStopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/Stops.json"
const val allStopsInfoUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/StopsInfo.json"
private val timeoutSeconds = 30.seconds

private fun getStopTimesResponse(stopCode: String) = Either.catch {
    val request = StopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.auth()
    }
    defaultClient.getStopTimes(request)
}.mapLeft(mapExceptionsF)

suspend fun getBusTimesResponse(stopCode: String) = either {
    val stopTimes = withTimeoutOrNull(timeoutSeconds) {
        CoroutineScope(Dispatchers.IO)
            .async { getStopTimesResponse(stopCode) }
            .await()
            .getOrNull()
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
        json
            .deserialized()
            .asArray()
            .bindMap()
            .mapNotNull { if (!it["stop_id"].asString().bindMap().contains("par")) null else it }
            .onEachAsync {
                it["cod_mode"] = it["stop_id"].asString().bindMap().substringAfter("_").substringBefore("_").toInt()
            }
            .onEachAsync {
                it["full_stop_code"] =
                    it["cod_mode"].asNumber().bindMap().toString() + "_" + it["stop_code"].asNumberOrString()
            }
            .onEachAsync {
                it["stop_code"] = it["stop_code"].asNumberOrString()
            }
            //This a hack to remove duplicates, since the same stop on metro can be repeated with different names
            .distinctBy {
                Pair(
                    if (it["cod_mode"].asNumber().bindMap().toString() == metroCodMode) 1 else randomUUID().toString(),
                    it["stop_name"].asString().bindMap()
                )
            }
            .distinctBy { it["stop_id"].asString().bindMap() }
            .asJson()
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

    val request = IncidentsAffectationsRequest().apply {
        this.codMode = codMode
        codLines = ArrayOfString()
        authentication = defaultClient.auth()
    }

    val result = withTimeoutOrNull(timeoutSeconds) {
        CoroutineScope(Dispatchers.IO)
            .async { defaultClient.getIncidentsAffectations(request) }
            .await()
    }

    if (result == null) throw BusTrackerException.InternalServerError("Got empty response")

    cachedAlerts.put(codMode, result)
    result
}.mapLeft(mapExceptionsF)

