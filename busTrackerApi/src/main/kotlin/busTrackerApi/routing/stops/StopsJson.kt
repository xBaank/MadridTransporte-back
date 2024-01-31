package busTrackerApi.routing.stops

import busTrackerApi.db.getItineraryByDestStop
import busTrackerApi.db.getItineraryByFullLineCode
import busTrackerApi.db.models.DeviceToken
import busTrackerApi.db.models.Stop
import busTrackerApi.db.models.StopsSubscription
import busTrackerApi.extensions.mapAsync
import busTrackerApi.extensions.toMiliseconds
import busTrackerApi.routing.stops.bus.busCodMode
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject

suspend fun parseStopTimesResponseToStopTimes(
    response: StopTimesResponse?,
    coordinates: Coordinates,
    name: String?,
    shortStopCode: String?
): StopTimes {
    val arrives = response?.stopTimes?.times?.time?.mapAsync {
        Arrive(
            direction = it.direction,
            lineCode = it.line.codLine,
            line = it.line.shortDescription,
            destination = it.destination,
            codMode = it.line.codMode.toInt(),
            estimatedArrive = it.time.toMiliseconds(),
            itineraryCode = getItineraryByDestStop(
                it.line.codLine,
                it.direction,
                it.destinationStop.codStop
            )?.itineraryCode ?: getItineraryByFullLineCode(
                it.line.codLine,
                it.direction
            )?.itineraryCode
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

fun buildStops(data: Flow<Stop>) = data.map {
    jObject {
        "stopCode" += it.stopCode
        "stopName" += it.stopName
        "stopLat" += it.stopLat
        "stopLon" += it.stopLon
        "codMode" += it.codMode
        "fullStopCode" += it.fullStopCode
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
                "lineCode" += arrive.value.first().lineCode
                "direction" += arrive.value.first().direction
                "anden" += arrive.value.first().anden
                "destination" += arrive.value.first().destination
                "estimatedArrives" += arrive.value.map { it.estimatedArrive.asJson() }.asJson()
                "itineraryCode" += arrive.value.first().itineraryCode
            }
        }
    }

}

fun buildSubscription(subscription: StopsSubscription, deviceToken: DeviceToken) = jObject {
    "stopCode" += subscription.stopCode.asJson()
    "codMode" += subscription.codMode.toInt().asJson()
    "stopName" += subscription.stopName.asJson()
    "simpleStopCode" += subscription.stopCode.split("_").getOrNull(1)?.asJson()
    "linesDestinations" += jArray {
        subscription.linesByDeviceToken[deviceToken.token]?.forEach {
            addObject {
                "line" += it.line.asJson()
                "destination" += it.destination.asJson()
                "codMode" += it.codMode.asJson()
            }
        }
    }
}