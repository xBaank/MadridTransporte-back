package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.onEachAsync
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.timed
import busTrackerApi.utils.csvToJson
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.hours

private val trainTimesCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private val trainStopsCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private const val chunks = 5
private const val stopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/train/paradas.csv"
private const val tripsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/train/trips.csv"
private const val routesUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/train/routes.csv"
private const val horariosUrl = "https://horarios.renfe.com/cer/HorariosServlet"

internal val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
internal val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val timeZoneMadrid = TimeZone.getTimeZone("Europe/Madrid")
private val hourTimeRegex = """\d{2}:\d{2}:\d{2}""".toRegex()

suspend fun getTrainTimesResponse(stopCode : String) = either {
    val times = getTrainTimes().bind()
    val stops = getTrainStops().bind()
    val trips = getTrainTrips().bind()
    val routes = getTrainRoutes().bind()
    val now = LocalTime.parse("12:00:00")

    val stop = stops
        .value
        .asArray()
        .bindMap()
        .find { it["IDESTACION"].asString().bindMap() == stopCode } ?:
        shift<Nothing>(InternalServerError("Stop not found"))

    val stopId = stop["CODIGOEMPRESA"].asString().bindMap()

    val nextTimes = times
        .value
        .asArray()
        .bindMap()
        .filter {
            it["stop_id"].asString().bindMap() == stopId &&
            it["arrival_time"].asString().map(::sanitizeLocalTime).bindMap().isAfter(now)
        }.asJson()

    nextTimes.onEachAsync {
        val routeId = trips.value.asArray().bindMap().firstOrNull { trip ->
            trip["trip_id"].asString().bindMap() == it["trip_id"].asString().bindMap()
        }?.get("route_id")?.asString()?.bindMap()?.trim() ?: shift<Nothing>(InternalServerError("Trip not found"))

        val route = routes.value.asArray().bindMap().firstOrNull { route ->
            route["route_id"].asString().bindMap().trim() == routeId
        } ?: shift<Nothing>(InternalServerError("Route not found"))

        it["route_short_name"] = route["route_short_name"]
            .asString()
            .bindMap()
            .substringBefore("-")
            .trim()

        it["route_long_name"] = route["route_long_name"]
            .asString()
            .bindMap()
            .substringBefore("-")
            .trim()

        it["stop_sequence"] = it["stop_sequence"]
            .asString()
            .bindMap()
            .trim()
    }

    println(nextTimes)
    TODO()

}

private suspend fun getTrainTimes() = either {
    val cached = trainTimesCache.get("trainTimes")
    if (cached != null) return@either cached

    val scope = CoroutineScope(Dispatchers.IO)

    val responses = (0 until chunks).map {
        scope.async { httpClient.get(getTrainStationsUrl(it)).await().body?.string() ?:
        shift(InternalServerError("Got empty response")) }
    }.awaitAll()

    val builder = StringBuilder()
    responses.forEach { builder.append(it) }

    val times = builder
        .toString()
        .let { csvToJson(it) }
        .asJson()
        .timed()

    trainTimesCache.put("trainTimes", times)
    times
}

private fun getTrainStationsUrl(index : Int) = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/train/horarios_madrid_$index.csv"
private suspend fun getTrainStops() = getNode(stopsUrl)
private suspend fun getTrainTrips() = getNode(tripsUrl)
private suspend fun getTrainRoutes() = getNode(routesUrl)
private suspend fun getNode(url: String) = either {
    val cached = trainStopsCache.get(url)
    if (cached != null) return@either cached

    val response = httpClient.get(url).await()
    val json = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
   val stops = csvToJson(json).asJson().timed()
    trainStopsCache.put(url, stops)
    stops
}

private suspend fun getRealTrainTimes(origin: String, destination: String) = either {
    val madridDate = LocalDateTime.now(timeZoneMadrid.toZoneId()).format(outputFormatter)
    val madridHour = LocalDateTime.now(timeZoneMadrid.toZoneId()).format(hourFormatter)
    val response = httpClient.post(horariosUrl, jObject {
        "nucleo" += "10" //Madrid Hardcoded
        "origen" += origin
        "destino" += destination
        "fchaViaje" += madridDate
        "validaReglaNegocio" += true
        "tiempoReal" += true
        "servicioHorarios" += "VTI"
        "horaViajeOrigen" += madridHour
        "horaViajeLlegada" += "26"
        "accesibilidadTrenes" += true
    }).await()
    val json = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
    json.deserialized().bindMap()
}

private fun sanitizeLocalTime(time : String): LocalTime {
    val timeParts= time.split(":")
    return LocalTime.of(timeParts[0].toInt() % 24, timeParts[1].toInt(), timeParts[2].toInt())
}