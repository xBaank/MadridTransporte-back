package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.*
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.timed
import busTrackerApi.utils.csvToJson
import io.github.reactivecircus.cache4k.Cache
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.hours

private val trainStopsCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private val trainTimesCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private const val stopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/train/paradas.csv"
private const val horariosUrl = "https://horarios.renfe.com/cer/HorariosServlet"

internal val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
internal val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val timeZoneMadrid = TimeZone.getTimeZone("Europe/Madrid")

suspend fun getTrainTimesResponseCached(originStopCode: String, destinationStopCode: String) = either {
    trainTimesCache.get(originStopCode + destinationStopCode) ?: shift(NotFound("No cached data"))
}
suspend fun getTrainTimesResponse(originStopCode : String, destinationStopCode: String) = either {
    val stops = getTrainStops().bind()

    val originId = stops
        .value
        .asArray()
        .bindMap()
        .firstOrNull {
            it["CODIGOESTACION"].asString().bindMap() == originStopCode
        }
        ?.get("CODIGOEMPRESA")
        ?.asString()
        ?.bindMap() ?:
        shift<Nothing>(NotFound("Origin stop not found"))

    val destinationId = stops
        .value
        .asArray()
        .bindMap()
        .firstOrNull {
            it["CODIGOESTACION"].asString().bindMap() == destinationStopCode
        }
        ?.get("CODIGOEMPRESA")
        ?.asString()
        ?.bindMap() ?:
        shift<Nothing>(NotFound("Destination stop not found"))

    val times = getRealTrainTimes(originId, destinationId).bind().timed()
    trainTimesCache.put(originStopCode + destinationStopCode, times)
    times
}

private suspend fun getTrainStops() = either {
    val cached = trainStopsCache.get(stopsUrl)
    if (cached != null) return@either cached

    val response = httpClient.get(stopsUrl).await()
    val json = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
   val stops = csvToJson(json).asJson().timed()
    trainStopsCache.put(stopsUrl, stops)
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

    if(!response.isSuccessful) shift<Nothing>(BadRequest("Renfe server not responding"))
    val json = response.body?.string() ?: shift<Nothing>(BadRequest("Renfe server returned empty response"))
    json.deserialized().bindMap()
}