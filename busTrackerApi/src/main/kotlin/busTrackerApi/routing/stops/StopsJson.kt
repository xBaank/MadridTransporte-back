package busTrackerApi.routing.stops

import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.StopTimesResponse
import crtm.soap.StopsByGeoLocationResponse
import crtm.utils.getCodStopFromStopCode
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.jObject

fun buildStopLocationsJson(stops: StopsByGeoLocationResponse) = jArray {
    stops.stops.stop.forEach { stop ->
        addObject {
            "codStop" += stop.codStop
            "simpleCodStop" += getCodStopFromStopCode(stop.codStop)
            "codMode" += stop.codMode
            "name" += stop.name
            "latitude" += stop.coordinates.latitude
            "longitude" += stop.coordinates.longitude
        }
    }
}

fun buildStopTimesJson(times: StopTimesResponse) = jObject {
    "name" += times.stopTimes.stop.name
    "codMode" += times.stopTimes.stop.codMode
    "times" += jArray {
        times.stopTimes?.times?.time?.forEach {
            addObject {
                "lineCode" += it.line.codLine
                "lineName" += it.line.shortDescription
                "codMode" += it.line.codMode
                "destination" += it.destination
                "codVehicle" += it.codVehicle
                "time" += it.time.toGregorianCalendar().time.toInstant().toEpochMilli()
            }
        }
    }
}

fun buildCachedJson(json: JsonNode, createdAt: Long) = jObject {
    "data" += json
    "lastTime" += createdAt
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