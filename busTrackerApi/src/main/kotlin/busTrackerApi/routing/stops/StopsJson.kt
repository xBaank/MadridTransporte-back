package busTrackerApi.routing.stops

import busTrackerApi.extensions.toMiliseconds
import busTrackerApi.routing.stops.bus.busCodMode
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesResponse
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject

fun parseStopTimesResponseToStopTimes(response: StopTimesResponse): StopTimes {
    val stopName = response.stopTimes.stop.name
    val arrives = response.stopTimes.times.time.map {
        Arrive(
            line = it.line.shortDescription,
            destination = it.destination,
            codMode = it.line.codMode.toInt(),
            estimatedArrive = it.time.toMiliseconds()
        )
    }

    return StopTimes(busCodMode.toInt(), stopName, arrives, emptyList())
}


fun buildAlertsJson(alerts: IncidentsAffectationsResponse) = jArray {
    alerts.incidentsAffectations.incidentAffectation.forEach {
        addObject {
            "description" += it.description
            "codMode" += it.codMode.toInt()
            "codLine" += it.codLine
            "stops" += it.stopsAffectated.shortStop.map { it.codStop.asJson() }.asJson()
        }
    }
}

fun buildJson(stopTimes: StopTimes) = jObject {
    "codMode" += stopTimes.codMode
    "stopName" += stopTimes.stopName
    val arrivesGroupedByLineAndDest = stopTimes.arrives.groupBy { Pair(it.line, it.destination) }
    "arrives" to jArray {
        arrivesGroupedByLineAndDest.forEach { arrive ->
            if (arrive.value.isEmpty()) return@forEach
            +jObject {
                "codMode" += arrive.value.first().codMode
                "line" += arrive.value.first().line
                "destination" += arrive.value.first().destination
                "estimatedArrives" += arrive.value.map { it.estimatedArrive.asJson() }.asJson()
            }
        }
    }
    "incidents" to jArray {
        stopTimes.incidents.forEach {
            +jObject {
                "title" += it.title
                "description" += it.description
                "cause" += it.cause
                "effect" += it.effect
                "urls" += it.url.map { it.asJson() }.asJson()
            }
        }
    }
}

fun buildCachedJson(json: JsonNode, createdAt: Long) = jObject {
    "data" += json
    "lastTime" += createdAt
}