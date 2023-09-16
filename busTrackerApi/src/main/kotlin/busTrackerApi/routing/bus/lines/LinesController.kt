package busTrackerApi.routing.bus.lines

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.routing.stops.timeoutSeconds
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.LineItinerary
import crtm.soap.LineItineraryRequest
import crtm.soap.LineLocationRequest
import crtm.soap.StopRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull

suspend fun getLocationsResponse(itinerary: LineItinerary, lineCode: String, codMode: String) = Either.catch {
    val lineRequest = LineLocationRequest().apply {
        this.codMode = codMode
        codLine = lineCode
        codVehicle = ""
        codItinerary = itinerary.codItinerary
        direction = itinerary.direction
        authentication = defaultClient.auth()
        codStop = itinerary.stops.shortStop.first().codStop
    }
    withTimeoutOrNull(timeoutSeconds) {
        CoroutineScope(Dispatchers.IO)
            .async { defaultClient.getLineLocation(lineRequest) }
            .await()
    } ?: throw SoapError("Server error")
}.mapLeft(mapExceptionsF)

suspend fun getItinerariesResponse(lineCode: String) = Either.catch {
    val itineraryRequest = LineItineraryRequest().apply {
        codLine = lineCode
        authentication = defaultClient.auth()
        active = 1
    }

    withTimeoutOrNull(timeoutSeconds) {
        CoroutineScope(Dispatchers.IO)
            .async {
                defaultClient
                defaultClient.getLineItineraries(itineraryRequest).takeIf { it.itineraries.lineItinerary.isNotEmpty() }
            }
            .await()
            ?: throw NotFound("No locations found for line $lineCode")
    } ?: throw SoapError("Server error")

}.mapLeft(mapExceptionsF)

suspend fun getStopsResponse(lineCode: String, codMode: String) = Either.catch {
    val request = StopRequest().apply {
        codLine = lineCode
        this.codMode = codMode
        authentication = defaultClient.auth()
    }
    withTimeoutOrNull(timeoutSeconds) {
        CoroutineScope(Dispatchers.IO)
            .async { defaultClient.getStops(request).takeIf { it.stops.stop.isNotEmpty() } }
            .await()
            ?: throw NotFound("No stops found for line $lineCode")
    } ?: throw SoapError("Server error")
}.mapLeft(mapExceptionsF)