package busTrackerApi.routing.stops.emt

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Coordinates
import busTrackerApi.routing.stops.Incident
import busTrackerApi.routing.stops.StopTimes
import crtm.utils.getCodStopFromStopCode
import simpleJson.*
import java.time.LocalDateTime
import java.time.ZoneOffset

suspend fun parseEMTToStopTimes(json: JsonNode) = either {
    val description =
        json["description"].asArray().getOrNull()?.firstOrNull { it["ES"].isRight() }?.get("ES")?.asString()
            ?.getOrNull()
    if (description != null) shift<Nothing>(BusTrackerException.NotFound(description))
    val stopName = json["data"][0]["StopInfo"][0]["stopName"].asString().bindMap()
    val coordinates = json["data"][0]["StopInfo"][0]["geometry"]["coordinates"].asArray().bindMap()
        .let { Coordinates(it[1].asDouble().bindMap(), it[0].asDouble().bindMap()) }
    val arrives = json["data"][0]["Arrive"].asArray().bindMap()
    val incidents = json["data"][0]["Incident"]["ListaIncident"]["data"].asArray().getOrNull()
    val arrivesMapped = arrives.map {
        val secondsToArrive = it["estimateArrive"].asLong().bindMap()
        val estimatedArrive = LocalDateTime.now().plusSeconds(secondsToArrive)
        Arrive(
            line = it["line"].asString().bindMap(),
            destination = it["destination"].asString().bindMap(),
            codMode = emtCodMode.toInt(),
            estimatedArrive = estimatedArrive.toInstant(ZoneOffset.UTC).toEpochMilli(),
        )
    }
    val incidentsMapped = incidents?.map {
        Incident(
            title = it["title"].asString().bindMap(),
            description = it["description"].asString().bindMap(),
            cause = it["cause"].asString().bindMap(),
            effect = it["effect"].asString().bindMap(),
            from = it["rssFrom"].asString().bindMap(),
            to = it["rssTo"].asString().bindMap(),
            url = it["moreInfo"]["@url"].asString().bindMap()
        )
    } ?: emptyList()
    StopTimes(
        emtCodMode.toInt(),
        stopName,
        coordinates,
        arrivesMapped,
        incidentsMapped,
        json["data"][0]["StopInfo"][0]["stopId"].asString().bindMap()
    )
}

fun createFailedTimes(name: String, coordinates: Coordinates) = StopTimes(
    codMode = emtCodMode.toInt(),
    stopName = name,
    coordinates = coordinates,
    arrives = null,
    incidents = emptyList(),
    simpleStopCode = getCodStopFromStopCode(name)
)