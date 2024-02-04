package api.routing.stops.emt

import api.db.getItineraryByFullLineCode
import api.db.getRoute
import api.exceptions.BusTrackerException
import api.extensions.bindJson
import api.extensions.mapAsync
import api.extensions.toDirection
import api.routing.stops.Arrive
import api.routing.stops.Coordinates
import api.routing.stops.Incident
import api.routing.stops.StopTimes
import arrow.core.continuations.either
import crtm.utils.createLineCode
import simpleJson.*
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

suspend fun extractEMTStopTimes(json: JsonNode) = either {
    val description =
        json["description"].asArray().getOrNull()?.firstOrNull { it["ES"].isRight() }?.get("ES")?.asString()
            ?.getOrNull()
    if (description != null) shift<Nothing>(BusTrackerException.NotFound(description))
    val stopName = json["data"][0]["StopInfo"][0]["stopName"].asString().bindJson()
    val coordinates = json["data"][0]["StopInfo"][0]["geometry"]["coordinates"].asArray().bindJson()
        .let { Coordinates(it[1].asDouble().bindJson(), it[0].asDouble().bindJson()) }
    val arrives = json["data"][0]["Arrive"].asArray().bindJson()
    val incidents = json["data"][0]["Incident"]["ListaIncident"]["data"].asArray().getOrNull()
    val arrivesMapped = arrives.mapAsync {
        val secondsToArrive = it["estimateArrive"].asLong().bindJson()
        val estimatedArrive = LocalDateTime.now(Clock.systemUTC()).plusSeconds(secondsToArrive)
        val line = it["line"].asString().bindJson()
        val linesInfo = json["data"][0]["StopInfo"][0]["lines"].asArray().bindJson()
        val direction =
            linesInfo.first { it["label"].asString().bindJson() == line }["to"].asString().bindJson().toDirection()
        val lineCode = getRoute(line, emtCodMode).getOrNull()?.fullLineCode ?: createLineCode(emtCodMode, line)
        Arrive(
            line = line,
            lineCode = lineCode,
            destination = it["destination"].asString().bindJson(),
            direction = direction,
            codMode = emtCodMode.toInt(),
            estimatedArrive = estimatedArrive.toInstant(ZoneOffset.UTC).toEpochMilli(),
            itineraryCode = getItineraryByFullLineCode(lineCode, direction)?.itineraryCode
        )
    }
    val incidentsMapped = incidents?.map {
        Incident(
            title = it["title"].asString().bindJson(),
            description = it["description"].asString().bindJson(),
            cause = it["cause"].asString().bindJson(),
            effect = it["effect"].asString().bindJson(),
            from = it["rssFrom"].asString().bindJson(),
            to = it["rssTo"].asString().bindJson(),
            url = it["moreInfo"]["@url"].asString().bindJson()
        )
    } ?: emptyList()
    StopTimes(
        emtCodMode.toInt(),
        stopName,
        coordinates,
        arrivesMapped,
        incidentsMapped,
        json["data"][0]["StopInfo"][0]["stopId"].asString().bindJson()
    )
}

fun createEMTFailedTimes(name: String, coordinates: Coordinates, stopCode: String) = StopTimes(
    codMode = emtCodMode.toInt(),
    stopName = name,
    coordinates = coordinates,
    arrives = null,
    incidents = emptyList(),
    simpleStopCode = stopCode
)