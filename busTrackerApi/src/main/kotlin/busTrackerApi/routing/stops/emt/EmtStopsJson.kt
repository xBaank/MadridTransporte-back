package busTrackerApi.routing.stops.emt

import arrow.core.continuations.either
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Incident
import busTrackerApi.routing.stops.StopTimes
import simpleJson.*
import java.time.LocalDateTime
import java.time.ZoneOffset

suspend fun parseEMTToStopTimes(json: JsonNode) = either {
    val stopName = json["data"][0]["StopInfo"][0]["stopName"].asString().bind()
    val arrives = json["data"][0]["Arrive"].asArray().bind()
    val incidents = json["data"][0]["Incident"]["ListaIncident"]["data"].asArray().bind()
    val arrivesMapped = arrives.map {
        val secondsToArrive = it["estimateArrive"].asLong().bind()
        val estimatedArrive = LocalDateTime.now().plusSeconds(secondsToArrive)
        Arrive(
            line = it["line"].asString().bind(),
            stop = it["stop"].asString().bind(),
            destination = it["destination"].asString().bind(),
            estimatedArrive = estimatedArrive.toInstant(ZoneOffset.UTC).toEpochMilli(),
        )
    }
    val incidentsMapped = incidents.map {
        Incident(
            title = it["title"].asString().bind(),
            description = it["description"].asString().bind(),
            cause = it["cause"].asString().bind(),
            effect = it["effect"].asString().bind(),
            url = it["moreInfo"]["@url"].asString().bind().let(::listOf)
        )
    }
    StopTimes(emtCodMode, stopName, arrivesMapped, incidentsMapped)
}