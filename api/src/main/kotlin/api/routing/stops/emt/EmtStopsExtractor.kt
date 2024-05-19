package api.routing.stops.emt

import api.extensions.toDirection
import api.routing.stops.Arrive
import api.routing.stops.Incident
import api.routing.stops.StopTimes
import arrow.core.raise.either
import common.exceptions.BusTrackerException
import common.extensions.bindJson
import common.models.Coordinates
import common.queries.getRoute
import common.utils.createLineCode
import common.utils.emtCodMode
import simpleJson.*
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

suspend fun extractEMTStopTimes(json: JsonNode) = either {
    val description =
        json["description"].asArray().getOrNull()?.firstOrNull { it["ES"].isRight() }?.get("ES")?.asString()
            ?.getOrNull()
    if (description != null) raise(BusTrackerException.NotFound(description))
    val stopName = json["data"][0]["StopInfo"][0]["stopName"].asString().bindJson()
    val coordinates = json["data"][0]["StopInfo"][0]["geometry"]["coordinates"].asArray().bindJson()
        .let { Coordinates(it[1].asDouble().bindJson(), it[0].asDouble().bindJson()) }
    val arrives = json["data"][0]["Arrive"].asArray().getOrNull()
    val incidents = json["data"][0]["Incident"]["ListaIncident"]["data"].asArray().getOrNull()
    val arrivesMapped = arrives?.map {
        val secondsToArrive = it["estimateArrive"].asLong().bindJson()
        val estimatedArrive = LocalDateTime.now(Clock.systemUTC()).plusSeconds(secondsToArrive)
        val line = it["line"].asString().bindJson()
        val linesInfo = json["data"][0]["StopInfo"][0]["lines"].asArray().getOrNull()
        val direction =
            linesInfo?.firstOrNull { it["label"].asString().bindJson() == line }?.get("to")?.asString()?.bindJson()
                ?.toDirection()
        val lineCode = getRoute(line, emtCodMode).getOrNull()?.fullLineCode ?: createLineCode(emtCodMode, line)
        Arrive(
            line = line,
            lineCode = lineCode,
            destination = it["destination"].asString().bindJson(),
            direction = direction,
            codMode = emtCodMode.toInt(),
            estimatedArrive = estimatedArrive.toInstant(ZoneOffset.UTC).toEpochMilli()
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
            url = it["moreInfo"]["@url"].asString().getOrNull() ?: ""
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