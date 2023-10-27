package busTrackerApi.routing.bus.lines

import arrow.core.Either
import busTrackerApi.db.Itinerary
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import busTrackerApi.utils.timeoutSeconds
import crtm.soap.LineItineraryRequest
import crtm.soap.LineLocationRequest
import crtm.soap.StopRequest
import kotlinx.coroutines.withTimeoutOrNull

suspend fun getLocationsResponse(itinerary: Itinerary, lineCode: String, codMode: String) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            codVehicle = ""
            codItinerary = itinerary.itineraryCode
            direction = itinerary.direction + 1
            authentication = defaultClient.value().auth()
            codStop = "8_" //In fact, it is optional, but we need to write something
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)

suspend fun getItinerariesResponse(lineCode: String) = Either.catch {
    val itineraryRequest = LineItineraryRequest().apply {
        codLine = lineCode
        authentication = defaultClient.value().auth()
        active = 1
    }

    withTimeoutOrNull(timeoutSeconds) {
        getSuspend(itineraryRequest, defaultClient.value()::getLineItinerariesAsync)
            .takeIf { it.itineraries.lineItinerary.isNotEmpty() }
            ?: throw NotFound("No locations found for line $lineCode")
    } ?: throw SoapError("Server error")

}.mapLeft(mapExceptionsF)

suspend fun getStopsResponse(lineCode: String, codMode: String) = Either.catch {
    val request = StopRequest().apply {
        codLine = lineCode
        this.codMode = codMode
        authentication = defaultClient.value().auth()
    }
    withTimeoutOrNull(timeoutSeconds) {
        getSuspend(request, defaultClient.value()::getStopsAsync)
            .takeIf { it.stops.stop.isNotEmpty() }
            ?: throw NotFound("No stops found for line $lineCode")
    } ?: throw SoapError("Server error")
}.mapLeft(mapExceptionsF)