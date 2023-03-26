package busTrackerApi.routing.lines

import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import simpleJson.jArray
import simpleJson.jObject

fun getLocations(itinerary : LineItinerary, lineCode: String, codMode: String): LineLocationResponse? {
    val lineRequest = LineLocationRequest().apply {
        this.codMode = codMode
        codLine = lineCode
        codVehicle = ""
        codItinerary = itinerary.codItinerary
        direction = itinerary.direction
        authentication = defaultClient.auth()
        codStop = itinerary.stops.shortStop.first().codStop
    }

    return try {
        defaultClient.getLineLocation(lineRequest)
    } catch (e: Exception) {
        return null
    }
}

fun getItineraries(lineCode: String): LineItineraryResponse? {
    val itineraryRequest = LineItineraryRequest().apply {
        codLine = lineCode
        authentication = defaultClient.auth()
        active = 1
    }

    return try {
        defaultClient.getLineItineraries(itineraryRequest).takeIf { it.itineraries.lineItinerary.isNotEmpty() }
    } catch (e: Exception) {
        return null
    }
}

fun getStops(lineCode: String, codMode: String): StopResponse? {
    val request = StopRequest().apply {
        codLine = lineCode
        this.codMode = codMode
        authentication = defaultClient.auth()
    }
    return defaultClient.getStops(request).takeIf { it.stops.stop.isNotEmpty() }
}

fun buildVehicleLocationJson(vehicleLocation: VehicleLocation) = jObject {
    "lineCode" += vehicleLocation.line.codLine
    "codVehicle" += vehicleLocation.codVehicle
    "coordinates" += jObject {
        "latitude" += vehicleLocation.coordinates.latitude
        "longitude" += vehicleLocation.coordinates.longitude
    }
    "direction" += vehicleLocation.direction
}

fun buildStopsJson(stop: Stop) = jObject {
    "codStop" += stop.codStop
    "name" += stop.name
    "coordinates" += jObject {
        "latitude" += stop.coordinates.latitude
        "longitude" += stop.coordinates.longitude
    }
}

fun buildItinerariesJson(itinerary: LineItinerary) = jObject {
    "codItinerary" += itinerary.codItinerary
    "direction" += itinerary.direction
    "stops" += jArray {
        itinerary.stops.shortStop.forEach {
            addObject {
                "codStop" += it.codStop
                "name" += it.name
                "codMode" += it.codMode
                "shortCodStop" += it.shortCodStop
            }
        }
    }
}