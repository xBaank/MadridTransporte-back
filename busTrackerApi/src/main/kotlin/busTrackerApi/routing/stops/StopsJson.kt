package busTrackerApi.routing.stops

import busTrackerApi.extensions.toMiliseconds
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesResponse
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject

fun parseStopTimesResponseToStopTimes(response: StopTimesResponse): StopTimes {
    val arrives = response.stopTimes.times.time.map {
        Arrive(
            line = it.line.shortDescription,
            stop = response.stopTimes.stop.name,
            destination = it.destination,
            estimatedArrive = it.time.toMiliseconds()
        )
    }

    return StopTimes(arrives, emptyList())
}


fun buildAlertsJson(alerts: IncidentsAffectationsResponse) = jArray {
    alerts.incidentsAffectations.incidentAffectation.forEach {
        addObject {
            "description" += it.description
            "codMode" += it.codMode
            "codLine" += it.codLine
            "stops" += it.stopsAffectated.shortStop.map { it.codStop.asJson() }.asJson()
        }
    }
}

fun buildJson(stopTimes : StopTimes) = jObject {
    "arrives" to jArray {
        stopTimes.arrives.forEach {
            +jObject {
                "line" += it.line
                "stop" += it.stop
                "destination" += it.destination
                "estimatedArrive" += it.estimatedArrive
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
                "url" += it.url
            }
        }
    }
}

fun buildCachedJson(json: JsonNode, createdAt: Long) = jObject {
    "data" += json
    "lastTime" += createdAt
}