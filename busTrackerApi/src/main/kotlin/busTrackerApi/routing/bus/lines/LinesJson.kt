package busTrackerApi.routing.bus.lines

import crtm.soap.LineItinerary
import crtm.soap.Stop
import crtm.soap.VehicleLocation
import simpleJson.jObject

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
    "stops" += simpleJson.jArray {
        itinerary.stops.shortStop.forEach {
            addObject {
                "codStop" += it.codStop
                "name" += it.name
                "codMode" += it.codMode
                "shortCodStop" += it.shortCodStop
            }
        }
    }
    "kml" += itinerary.kml
}