package busTrackerApi.routing.bus.lines

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.LineItinerary
import crtm.soap.LineItineraryRequest
import crtm.soap.LineLocationRequest
import crtm.soap.StopRequest

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