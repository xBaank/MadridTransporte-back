package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.getStopById
import busTrackerApi.routing.stops.timed
import busTrackerApi.utils.parse
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

//Good job renfe, ty for providing an easy way to get the times

private val trainUrlCaches = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private val trainTimesCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private const val tramosUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/TrainTramos.csv"
private const val trainStationsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/TrainStations.csv"
private const val horariosUrl = "https://horarios.renfe.com/cer/HorariosServlet"

private val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
private val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val timeZoneMadrid = TimeZone.getTimeZone("Europe/Madrid")


suspend fun getTrainTimesResponse(stopCode: String) = either {
    val getStopById = getStopById(stopCode).bind()
    val stationName = getStopById["name"].asString().bindMap()
    val trainLines = getTrainLines().bind().value.asArray().bindMap()
    val lastAndFirst = getLastAndFirstStop(trainLines, stationName).bind()
    val normalized = getStationsNormalized(lastAndFirst).bind().distinctBy { it.lastStopCode }
    val times = normalized.map {
        CoroutineScope((Dispatchers.IO)).async { getTrainTimes(it.stationCode, it.lastStopCode).bind() }
    }
    val result = replaceTrans(buildTrainJson(times.awaitAll().asJson())).bind().timed()
    trainTimesCache.put(stopCode, result)
    result
}

suspend fun getTrainTimesCachedResponse(stopCode: String) = either {
    trainTimesCache.get(stopCode) ?: shift(NotFound("No cached data found"))
}


private suspend fun getTrainLines() = either {
    val cached = trainUrlCaches.get(tramosUrl)
    if (cached != null) return@either cached
    val response = httpClient.get(tramosUrl).await()
    val csv = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
    val parsed = parse(csv).timed()
    trainUrlCaches.put(tramosUrl, parsed)
    parsed
}

private suspend fun getStationsNormalized(data: List<SimpleTrainStopData>) = either {
    val cached = trainUrlCaches.get(trainStationsUrl) ?: run {
        val response = httpClient.get(trainStationsUrl).await()
        val csv = response.body?.string() ?: shift<Nothing>(InternalServerError("Got empty response"))
        val parsed = parse(csv).timed()
        trainUrlCaches.put(trainStationsUrl, parsed)
        parsed
    }

    val parsed = cached.value.asArray().bindMap()

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

        TrainStopData(
            line = it.line,
            stationName = stationName,
            stationCode = stationCode,
            lastStop = lastStop,
            lastStopCode = lastStopCode
        )
    }
}

private suspend fun getTrainTimes(origin: String, destination: String) = either {
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

private suspend fun getLastAndFirstStop(data: JsonArray, stopName: String) = either {
    val stops = data.filter { it["DENOMINACION"].asString().getOrNull() == stopName }
    val lines = stops.map {
        val sentido = it["SENTIDO"].asString().bindMap()
        val line = it["CODIGOGESTIONLINEA"].asString().bindMap()

        val lastStop = data.filter { it["CODIGOGESTIONLINEA"].asString().bindMap() == line }
            .filter { it["SENTIDO"].asString().bindMap() == sentido }
            .maxByOrNull { it["NUMEROORDEN"].asString().bindMap().toInt() }
            ?: shift<Nothing>(InternalServerError("Couldn't find last stop for line $line"))

        SimpleTrainStopData(
            line = line,
            stationName = stopName,
            lastStop = lastStop["DENOMINACION"].asString().bindMap(),
        )
    }
    lines
}

private suspend fun replaceTrans(json: JsonNode) = either {
    json["times"].asArray().bindMap().forEach { time ->
        time["horarios"].asArray().bindMap().forEach {
            val trans = it["trans"].asArray().getOrNull()?.firstOrNull() ?: return@forEach

            json["name"] = trans["descEstacion"].asString().bindMap()
            time["destination"] = trans["descEstacion"].asString().bindMap()
            json["horaLlegada"] = trans["horaLlegada"].asString().bindMap()

            it.asObject().bindMap().value.remove("trans")
        }
    }
    json["times"] = json["times"].asArray().bindMap().distinctBy { it["destination"] }.asJson()
    json
}