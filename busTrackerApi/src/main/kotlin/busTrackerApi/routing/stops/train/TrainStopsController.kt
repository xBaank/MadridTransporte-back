package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.timed
import busTrackerApi.utils.hourFormatter
import busTrackerApi.utils.timeZoneMadrid
import io.github.reactivecircus.cache4k.Cache
import ru.gildor.coroutines.okhttp.await
import simpleJson.JsonNode
import simpleJson.deserialized
import simpleJson.jObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.hours

private val trainTimesCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
private const val horariosUrl = "https://horarios.renfe.com/cer/HorariosServlet"

suspend fun getTrainTimesResponseCached(originStopCode: String, destinationStopCode: String) = either {
    trainTimesCache.get(originStopCode + destinationStopCode) ?: shift(NotFound("No cached data"))
}

suspend fun getTrainTimesResponse(originId: String, destinationId: String) = either {
    val times = getRealTrainTimes(originId, destinationId).bind().timed()
    trainTimesCache.put(originId + destinationId, times)
    times
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

    if (!response.isSuccessful) shift<Nothing>(BadRequest("Renfe server not responding"))
    val json = response.body?.bytes()
        ?.let { String(it, Charsets.ISO_8859_1) }
        ?.toByteArray(Charsets.ISO_8859_1)
        ?.toString(Charsets.UTF_8)
        ?: shift<Nothing>(BadRequest("Renfe server returned empty response"))
    json.deserialized().bindMap()
}