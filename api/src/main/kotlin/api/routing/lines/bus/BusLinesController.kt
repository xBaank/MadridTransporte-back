package api.routing.lines.bus

import api.config.EnvVariables.timeoutSeconds
import api.db.models.ItineraryWithStops
import api.exceptions.BusTrackerException.SoapError
import api.extensions.getSuspend
import api.utils.auth
import api.utils.defaultClient
import api.utils.mapExceptionsF
import arrow.core.Either
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