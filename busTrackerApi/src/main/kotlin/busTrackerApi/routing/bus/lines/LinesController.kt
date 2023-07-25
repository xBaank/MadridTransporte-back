package busTrackerApi.routing.bus.lines

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import simpleJson.jArray
import simpleJson.jObject

fun getLocationsResponse(itinerary: LineItinerary, lineCode: String, codMode: String) = Either.catch {
    val lineRequest = LineLocationRequest().apply {
        this.codMode = codMode
        codLine = lineCode
        codVehicle = ""
        codItinerary = itinerary.codItinerary
        direction = itinerary.direction
        authentication = defaultClient.auth()
        codStop = itinerary.stops.shortStop.first().codStop
    }

    defaultClient.getLineLocation(lineRequest)
}.mapLeft(mapExceptionsF)

fun getItinerariesResponse(lineCode: String) = Either.catch {
    val itineraryRequest = LineItineraryRequest().apply {
        codLine = lineCode
        authentication = defaultClient.auth()
        active = 1
    }

    defaultClient.getLineItineraries(itineraryRequest).takeIf { it.itineraries.lineItinerary.isNotEmpty() } ?:
    throw NotFound("No itineraries found for line $lineCode")
}.mapLeft(mapExceptionsF)

fun getStopsResponse(lineCode: String, codMode: String) = Either.catch {
    val request = StopRequest().apply {
        codLine = lineCode
        this.codMode = codMode
        authentication = defaultClient.auth()
    }
    defaultClient.getStops(request).takeIf { it.stops.stop.isNotEmpty() } ?:
    throw NotFound("No stops found for line $lineCode")
}.mapLeft(mapExceptionsF)

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
    "kml" += itinerary.kml
}