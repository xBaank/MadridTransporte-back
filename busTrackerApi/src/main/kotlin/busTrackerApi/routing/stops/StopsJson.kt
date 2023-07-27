package busTrackerApi.routing.stops

import busTrackerApi.extensions.removeNonSpacingMarks
import crtm.soap.IncidentsAffectationsResponse
import crtm.soap.ShortStopTimesResponse
import crtm.soap.StopsByGeoLocationResponse
import crtm.utils.getCodModeFromLineCode
import crtm.utils.getCodStopFromStopCode
import simpleJson.*

fun buildStopsJson(stops: JsonNode) = jArray {
    stops.asArray().getOrNull()?.forEach { stop ->
        addObject {
            "codStop" += stop["codStop"].asString().getOrNull()
            "simpleCodStop" += getCodStopFromStopCode(stop["codStop"].asString().getOrNull()!!)
            "codMode" += stop["codMode"].asString().getOrNull()
            "name" += stop["name"].asString().getOrNull()?.removeNonSpacingMarks()
            "latitude" += stop["coordinates"]["latitude"].asNumber().getOrNull()
            "longitude" += stop["coordinates"]["longitude"].asNumber().getOrNull()
            "lines" += stop["lines"].asArray().getOrNull() ?: jArray {}
        }
    }
}

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

fun buildStopTimesJson(times: ShortStopTimesResponse) = jObject {
    "name" += times.stopTimes.stop.name
    "times" += jArray {
        times.stopTimes?.times?.shortTime?.forEach {
            addObject {
                "lineCode" += it.codLine
                "codMode" += getCodModeFromLineCode(it.codLine)
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