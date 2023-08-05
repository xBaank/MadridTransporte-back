package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.getCodigoEmpresaByStopCode
import busTrackerApi.routing.stops.timed
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.hours

private val cache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val timeZoneMadrid = TimeZone.getTimeZone("Europe/Madrid")

private const val horariosUrl = "https://horarios.renfe.com/cer/HorariosServlet"
private const val itinerariosUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/train/itineraries.json"

suspend fun getTrainTimesResponseCached(originStopCode: String) = either {
    cache.get(originStopCode) ?: shift(NotFound("No cached data"))
}

suspend fun getTrainTimesResponse(stopCodeOrigin: String) = either {
    val coroutine = CoroutineScope(Dispatchers.IO)
    val times = getAllRoutes(stopCodeOrigin)
        .bind()
        .map { coroutine.async { getRealTrainTimes(it.stationId, it.destinationId).bind() } }
        .awaitAll()
        .asJson()
        .timed()
    cache.put(stopCodeOrigin, times)
    times
}

private suspend fun getAllRoutes(stopCodeOrigin: String) = either {
    val allItineraries = getItineraries()
        .bind()
        .value
        .asArray()
        .bindMap()

    val itineraries = allItineraries
        .filter { it["IDFESTACION"].asString().bindMap() == stopCodeOrigin }

    itineraries.mapNotNull { itinerary ->
        val nextStop = allItineraries
            .filter { it["CODIGOITINERARIO"].asInt().bindMap() == itinerary["CODIGOITINERARIO"].asInt().bindMap() }
            .firstOrNull { it["NUMEROORDEN"].asInt().bindMap() == itinerary["NUMEROORDEN"].asInt().bindMap() + 1 }
            ?: return@mapNotNull null

        if(nextStop["IDFESTACION"].asString().bindMap() == stopCodeOrigin) return@mapNotNull null

        val destinationInfo = getCodigoEmpresaByStopCode(nextStop["IDFESTACION"].asString().bindMap()).bind()
        val originInfo = getCodigoEmpresaByStopCode(stopCodeOrigin).bind()

        TrainRoute(originInfo.toString(), destinationInfo.toString())
    }
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

private suspend fun getItineraries() = either {
    val cached = cache.get(itinerariosUrl)
    if(cached != null) return@either cached
    val response = httpClient.get(itinerariosUrl).await()
    if(!response.isSuccessful) shift<Nothing>(BadRequest("Static server not responding"))
    val json = response.body?.string() ?: shift<Nothing>(BadRequest("Static server returned empty response"))
    val result = json.deserialized().bindMap().timed()
    cache.put(itinerariosUrl, result)
    result
}