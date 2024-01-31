package busTrackerApi.routing.lines.bus

import arrow.core.Either
import busTrackerApi.config.EnvVariables.timeoutSeconds
import busTrackerApi.db.models.ItineraryWithStops
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import crtm.soap.LineLocationRequest
import kotlinx.coroutines.withTimeoutOrNull

suspend fun getLocationsResponse(itinerary: ItineraryWithStops, lineCode: String, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            codItinerary = itinerary.itineraryCode
            direction = itinerary.direction + 1
            authentication = defaultClient.value().auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)