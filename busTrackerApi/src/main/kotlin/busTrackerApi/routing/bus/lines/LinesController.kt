package busTrackerApi.routing.bus.lines

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.routing.stops.timeoutSeconds
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import crtm.soap.LineItinerary
import crtm.soap.LineItineraryRequest
import crtm.soap.LineLocationRequest
import crtm.soap.StopRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

suspend fun getLocationsResponse(itinerary: LineItinerary, lineCode: String, codMode: String) = Either.catch {
    val lineRequest = LineLocationRequest().apply {
        this.codMode = codMode
        codLine = lineCode
        codVehicle = ""
        codItinerary = itinerary.codItinerary
        direction = itinerary.direction
        authentication = defaultClient.await().auth()
        codStop = itinerary.stops.shortStop.first().codStop
    }
    withTimeoutOrNull(timeoutSeconds) {
        withContext(Dispatchers.IO) { defaultClient.await().getLineLocation(lineRequest) }
    } ?: throw SoapError("Server error")
}.mapLeft(mapExceptionsF)

suspend fun getItinerariesResponse(lineCode: String) = Either.catch {
    val itineraryRequest = LineItineraryRequest().apply {
        codLine = lineCode
        authentication = defaultClient.await().auth()
        active = 1
    }

    withTimeoutOrNull(timeoutSeconds) {
        withContext(Dispatchers.IO) {
            defaultClient.await().getLineItineraries(itineraryRequest)
                .takeIf { it.itineraries.lineItinerary.isNotEmpty() }
        }
            ?: throw NotFound("No locations found for line $lineCode")
    } ?: throw SoapError("Server error")

}.mapLeft(mapExceptionsF)

suspend fun getStopsResponse(lineCode: String, codMode: String) = Either.catch {
    val request = StopRequest().apply {
        codLine = lineCode
        this.codMode = codMode
        authentication = defaultClient.await().auth()
    }
    withTimeoutOrNull(timeoutSeconds) {
        withContext(Dispatchers.IO) { defaultClient.await().getStops(request).takeIf { it.stops.stop.isNotEmpty() } }
            ?: throw NotFound("No stops found for line $lineCode")
    } ?: throw SoapError("Server error")
}.mapLeft(mapExceptionsF)