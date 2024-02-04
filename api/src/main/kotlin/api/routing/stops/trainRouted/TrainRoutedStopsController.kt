package api.routing.stops.trainRouted

import api.config.httpClient
import api.exceptions.BusTrackerException.InternalServerError
import api.extensions.bindJson
import api.extensions.post
import api.utils.hourFormatter
import api.utils.timeZoneMadrid
import arrow.core.continuations.either
import ru.gildor.coroutines.okhttp.await
import simpleJson.deserialized
import simpleJson.jObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
private const val horariosUrl = "https://horarios.renfe.com/cer/HorariosServlet"

suspend fun getTrainRoutedTimesResponse(origin: String, destination: String) = either {
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

    if (!response.isSuccessful) shift<Nothing>(InternalServerError("Renfe server not responding"))
    val json = response.body?.bytes()
        ?.let { String(it, Charsets.ISO_8859_1) }
        ?.toByteArray(Charsets.ISO_8859_1)
        ?.toString(Charsets.UTF_8)
        ?: shift<Nothing>(InternalServerError("Renfe server returned empty response"))
    json.deserialized().bindJson()
}