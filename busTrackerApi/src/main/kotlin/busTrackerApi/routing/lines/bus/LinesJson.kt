package busTrackerApi.routing.lines.bus

import busTrackerApi.db.Itinerary
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
    "service" += vehicleLocation.service
}

fun buildItinerariesJson(itinerary: Itinerary) = jObject {
    "codItinerary" += itinerary.itineraryCode
    "direction" += itinerary.direction + 1
    "stops" += simpleJson.jArray {
        itinerary.stops.forEach {
            addObject {
                "fullStopCode" += it.fullStopCode
                "order" += it.order
            }
        }
    }
}