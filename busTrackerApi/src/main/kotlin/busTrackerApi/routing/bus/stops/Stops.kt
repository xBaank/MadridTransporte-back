package busTrackerApi.routing.bus.stops

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.EstimationsRequest
import crtm.soap.EstimationsResponse
import crtm.soap.ShortStopTimesRequest
import crtm.soap.ShortStopTimesResponse
import crtm.utils.getCodModeFromLineCode
import io.github.reactivecircus.cache4k.Cache
import io.ktor.server.websocket.*
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

val stopEstimationsCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

val subscribedStops = mutableMapOf<String, WebSocketServerSession>()

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
}.mapLeft(mapExceptionsF)

fun getEstimations(stopCode: String) = Either.catch {
    val fromCache = stopEstimationsCache.get(stopCode)
    if (fromCache != null) return@catch fromCache

    val request = EstimationsRequest().apply {
        this.stopCode += stopCode
        type = 1
        orderBy = 2
        authentication = defaultClient.auth()
    }
    val estimations = defaultClient.getEstimations(request)
    if (estimations.stopsEstimations.stopEstimations.isEmpty()) throw NotFound("No estimations found")
    val json = buildStopEstimationsJson(estimations).timed()
    stopEstimationsCache.put(stopCode, json)
    json
}.mapLeft(mapExceptionsF)


suspend fun getTimes(stopCode: String, codMode: String?) =
    getTimes(stopCode, codMode, ::getStopTimes)
        .mapLeft(mapExceptionsF)

suspend fun getTimesOrCached(stopCode: String, codMode: String?) =
    getTimes(stopCode, codMode)
        .onRight { stopTimesCache.put(stopCode, it) }
        .getOrElse { stopTimesCache.get(stopCode) }?.right() ?:
        NotFound("No stop times found for stop code $stopCode").left()



private suspend inline fun <T> getTimes(
    stopCode: String,
    codMode: String?,
    crossinline getF: (stopCode: String, codMode: String?) -> Either<T, ShortStopTimesResponse>
) = withTimeoutOrNull(20.seconds) {
    val stopTimes = CoroutineScope(Dispatchers.IO)
        .async { getF(stopCode, codMode) }
        .await()
        .getOrNull()

    stopTimes?.let(::buildStopTimesJson)?.asJson()?.timed()
}?.right() ?: NotFound("No stop times found for stop code $stopCode").left()

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

fun buildStopEstimationsJson(estimations: EstimationsResponse) = jObject {
    val stop = estimations.stopsEstimations.stopEstimations.firstOrNull()
    "name" += stop?.stopName
    "estimatedTimes" += stop?.timesInfo?.timeInfo?.map { estimation ->
        jObject {
            "lineCode" += estimation.line
            "codMode" += getCodModeFromLineCode(estimation.line)
            "codVehicle" += estimation.vehicleCode
            "time" += estimation.time.toGregorianCalendar().time.toInstant().toEpochMilli()
        }
    }?.asJson() ?: jArray()
}