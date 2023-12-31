package busTrackerApi.routing.lines

import busTrackerApi.db.models.ItineraryWithStops
import busTrackerApi.db.models.Shape
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

fun buildItineraryJson(itinerary: ItineraryWithStops) = jObject {
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

fun buildShapeJson(shape: Shape) = jObject {
    "latitude" += shape.latitude
    "longitude" += shape.longitude
    "sequence" += shape.sequence
}