package api.routing.stops

import common.models.DeviceToken
import common.models.Stop
import common.models.StopOrderWithItineraries
import common.models.StopsSubscription
import common.queries.getRouteByFullLineCode
import common.utils.getCodModeFromLineCode
import crtm.soap.IncidentsAffectationsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject

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
        "wheelchair" += it.wheelchair
        "zone" += it.zone
    }
}

fun buildStopTimesJson(stopTimes: StopTimes) = jObject {
    "codMode" += stopTimes.codMode
    "stopName" += stopTimes.stopName
    "simpleStopCode" += stopTimes.simpleStopCode
    "stopCode" += stopTimes.stopCode
    "coordinates" += jObject {
        "latitude" += stopTimes.coordinates.stopLat
        "longitude" += stopTimes.coordinates.stopLon
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
    val arrivesGroupedByLineAndDest = stopTimes.arrives
        ?.sortedBy { it.line.toIntOrNull() }
        ?.groupBy { Triple(it.line, it.destination, it.anden) }
    if (arrivesGroupedByLineAndDest == null) {
        "arrives" += null
        return@jObject
    }
    "arrives" to jArray {
        arrivesGroupedByLineAndDest.forEach { arrive ->
            if (arrive.value.isEmpty()) return@forEach
            addObject {
                "codMode" += arrive.value.first().codMode
                "line" += arrive.value.first().line
                "lineCode" += arrive.value.first().lineCode
                "direction" += arrive.value.first().direction
                "anden" += arrive.value.first().anden
                "destination" += arrive.value.first().destination
                "estimatedArrives" += arrive.value.map { it.estimatedArrive.asJson() }.asJson()
            }
        }
    }

}

suspend fun buildStopTimesPlannedJson(stopTimes: List<StopOrderWithItineraries>) = stopTimes.map {
    object {
        val lineCode = it.itineraries.first().fullLineCode
        val itineraryCode = it.itineraries.first().itineraryCode
        val itineraryName = it.itineraries.first().tripName
        val direction = it.itineraries.first().direction
        val departureTime = it.departureTime
    }
}.groupBy {
    Triple(it.lineCode, it.direction, it.itineraryCode)
}.map {
    jObject {
        val route = getRouteByFullLineCode(it.key.first).getOrNull()
        "fullLineCode" += it.key.first
        "lineCode" += route?.simpleLineCode
        "destination" += it.value.first().itineraryName
        "codMode" += (route?.codMode ?: getCodModeFromLineCode(it.key.first)).toIntOrNull()
        "direction" += it.key.second + 1
        "itineraryCode" += it.key.third
        "arrives" += jArray {
            it.value.distinctBy { it.departureTime }.sortedBy { it.departureTime }.forEach {
                add(it.departureTime)
            }
        }
    }
}.asJson()

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