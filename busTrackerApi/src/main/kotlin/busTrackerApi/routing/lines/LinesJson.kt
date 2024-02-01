package busTrackerApi.routing.lines

import busTrackerApi.db.models.ItineraryWithStops
import busTrackerApi.db.models.Shape
import simpleJson.JsonObject
import simpleJson.jArray
import simpleJson.jObject

fun buildVehicleLocationJson(vehicleLocations: VehicleLocations): JsonObject = jObject {
    "codMode" += vehicleLocations.codMode
    "lineCode" += vehicleLocations.lineCode
    "locations" += jArray {
        vehicleLocations.locations.forEach {
            addObject {
                "lineCode" += it.line.codLine
                "simpleLineCode" += it.line.shortDescription
                "codVehicle" += it.codVehicle
                "coordinates" += jObject {
                    "latitude" += it.coordinates.latitude
                    "longitude" += it.coordinates.longitude
                }
                "direction" += it.direction
                "service" += it.service
            }
        }
    }
}

fun buildItineraryJson(itinerary: ItineraryWithStops) = jObject {
    "codItinerary" += itinerary.itineraryCode
    "direction" += itinerary.direction + 1
    "stops" += jArray {
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
    "distance" += shape.distance
}