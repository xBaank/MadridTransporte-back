package api.routing.lines

import common.models.Itinerary
import common.models.ItineraryWithStops
import common.models.RouteWithItineraries
import common.models.Shape
import simpleJson.JsonObject
import simpleJson.asJson
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

fun buildRouteJson(route: RouteWithItineraries) = jObject {
    "fullLineCode" += route.fullLineCode
    "simpleLineCode" += route.simpleLineCode
    "codMode" += route.codMode.toInt()
    "routeName" += route.routeName
    "itineraries" += route.itineraries.distinctBy(Itinerary::itineraryCode).map(::buildItinerary).asJson()
}

fun buildItinerary(itinerary: Itinerary) = jObject {
    "itineraryCode" += itinerary.itineraryCode
    "direction" += itinerary.direction + 1
    "tripName" += itinerary.tripName
    "serviceId" += itinerary.serviceId
}