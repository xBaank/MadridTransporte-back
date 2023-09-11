package busTrackerApi.routing.stops

import busTrackerApi.extensions.toMiliseconds
import busTrackerApi.routing.stops.bus.busCodMode
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesResponse
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject

fun parseStopTimesResponseToStopTimes(
    response: StopTimesResponse?,
    coordinates: Coordinates,
    name: String?,
    shortStopCode: String?
): StopTimes {
    val arrives = response?.stopTimes?.times?.time?.map {
        Arrive(
            line = it.line.shortDescription,
            destination = it.destination,
            codMode = it.line.codMode.toInt(),
            estimatedArrive = it.time.toMiliseconds()
        )
    }

    return StopTimes(
        busCodMode.toInt(),
        name ?: response?.stopTimes?.stop?.name ?: "",
        coordinates,
        arrives?.sortedBy { it.line.toIntOrNull() },
        emptyList(),
        shortStopCode ?: response?.stopTimes?.stop?.shortCodStop ?: "",
    )
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

fun buildStopTimesJson(stopTimes: StopTimes) = jObject {
    "codMode" += stopTimes.codMode
    "stopName" += stopTimes.stopName
    "simpleStopCode" += stopTimes.simpleStopCode
    "stopCode" += stopTimes.stopCode
    "coordinates" += jObject {
        "latitude" += stopTimes.coordinates.latitude
        "longitude" += stopTimes.coordinates.longitude
    }
    "incidents" to jArray {
        stopTimes.incidents.forEach {
            +jObject {
                "title" += it.title
                "description" += it.description
                "from" += it.from
                "to" += it.to
                "cause" += it.cause
                "effect" += it.effect
                "url" += it.url
            }
        }
    }
    val arrivesGroupedByLineAndDest = stopTimes.arrives?.groupBy { Pair(it.line, it.destination) }
    if (arrivesGroupedByLineAndDest == null) {
        "arrives" += null
        return@jObject
    }
    "arrives" to jArray {
        arrivesGroupedByLineAndDest.forEach { arrive ->
            if (arrive.value.isEmpty()) return@forEach
            +jObject {
                "codMode" += arrive.value.first().codMode
                "line" += arrive.value.first().line
                "anden" += arrive.value.first().anden
                "destination" += arrive.value.first().destination
                "estimatedArrives" += arrive.value.map { it.estimatedArrive.asJson() }.asJson()
            }
        }
    }

}

fun buildSubscription(subscription: StopsSubscription, deviceToken: String) = jObject {
    "stopCode" += subscription.stopCode.asJson()
    "codMode" += subscription.codMode.toInt().asJson()
    "stopName" += subscription.stopName.asJson()
    "simpleStopCode" += subscription.stopCode.split("_").getOrNull(1)?.asJson()
    "linesDestinations" += jArray {
        subscription.linesByDeviceToken[deviceToken]?.forEach {
            addObject {
                "line" += it.line.asJson()
                "destination" += it.destination.asJson()
                "codMode" += it.codMode.asJson()
            }
        }
    }
}