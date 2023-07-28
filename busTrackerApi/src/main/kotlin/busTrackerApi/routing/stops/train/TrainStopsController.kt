package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.getStopById
import busTrackerApi.utils.parse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//Good job renfe, ty for providing an easy way to get the times

private val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val timeZoneMadrid = TimeZone.getTimeZone("Europe/Madrid")

private suspend fun getTrainLines() = either {
    val response = httpClient.get("https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/TrainTramos.csv").await()
    val csv = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
    val parsed = parse(csv)
    parsed
}

private suspend fun getStationsNormalized(data: List<SimpleTrainData>) = either {
    val response = httpClient.get("https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/TrainStations.csv").await()
    val csv = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
    val parsed = parse(csv)
    data.map {
        val stationName = it.stationName

        val stationCode = parsed.firstOrNull { it["DENOMINACION"].asString().bindMap() == stationName }
            ?.get("CODIGOEMPRESA")
            ?.asString()
            ?.bindMap() ?:
            shift<Nothing>(InternalServerError("Station $stationName not found in csv"))

       val lastStop = it.lastStop

        val lastStopCode = parsed.firstOrNull { it["DENOMINACION"].asString().bindMap() == lastStop }
            ?.get("CODIGOEMPRESA")
            ?.asString()
            ?.bindMap() ?:
            shift<Nothing>(InternalServerError("Station $lastStop not found in csv"))

        TrainData(
            line = it.line,
            stationName = stationName,
            stationCode = stationCode,
            lastStop = lastStop,
            lastStopCode = lastStopCode
        )
    }
}

suspend fun getTrainTimes(stopCode: String) = either {
    val getStopById = getStopById(stopCode).bind()
    val stationName = getStopById["name"].asString().bindMap()
    val trainLines = getTrainLines().bind()
    val lastAndFirst = getLastAndFirstStop(trainLines, stationName).bind()
    val normalized = getStationsNormalized(lastAndFirst).bind()
    val times = normalized.map {
        CoroutineScope((Dispatchers.IO)).async { getTrainTimes(it.stationCode, it.lastStopCode).bind() }
    }
    times.awaitAll().asJson()
}

private suspend fun getTrainTimes(origin: String, destination: String) = either {
    val madridDate = LocalDateTime.now(timeZoneMadrid.toZoneId()).format(outputFormatter)
    val madridHour = LocalDateTime.now(timeZoneMadrid.toZoneId()).format(hourFormatter)
    val response = httpClient.post("https://horarios.renfe.com/cer/HorariosServlet", jObject {
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

private suspend fun getLastAndFirstStop(data: JsonArray, stopName: String) = either {
    val stops = data.filter { it["DENOMINACION"].asString().getOrNull() == stopName }
    val lines = stops.map {
        val sentido = it["SENTIDO"].asString().bindMap()
        val line = it["CODIGOGESTIONLINEA"].asString().bindMap()

        val lastStop = data.filter { it["CODIGOGESTIONLINEA"].asString().bindMap() == line }
            .filter { it["SENTIDO"].asString().bindMap() == sentido }
            .maxByOrNull { it["NUMEROORDEN"].asString().bindMap().toInt() }
            ?: shift<Nothing>(InternalServerError("Couldn't find last stop for line $line"))

        SimpleTrainData(
            line = line,
            stationName = stopName,
            lastStop = lastStop["DENOMINACION"].asString().bindMap(),
        )
    }
    lines
}