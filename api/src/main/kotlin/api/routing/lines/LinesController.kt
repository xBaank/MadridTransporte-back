package api.routing.lines

import api.config.EnvVariables.timeoutSeconds
import api.utils.auth
import api.utils.defaultClient
import api.utils.getSuspend
import api.utils.mapExceptionsF
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import common.exceptions.BusTrackerException.NotFound
import common.exceptions.BusTrackerException.SoapError
import common.models.ItineraryWithStops
import common.models.StopOrder
import crtm.soap.LineItineraryRequest
import crtm.soap.LineLocationRequest
import kotlinx.coroutines.withTimeoutOrNull

suspend fun getLocationsResponse(lineCode: String, direction: Int, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            this.direction = direction
            authentication = auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)

suspend fun getLocationsResponse(lineCode: String, itineraryCode: String, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            codItinerary = itineraryCode
            authentication = auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)

suspend fun getItinerariesResponse(lineCode: String, direction: Int) =
    Either.catch {
        val lineRequest = LineItineraryRequest().apply {
            codLine = lineCode
            authentication = auth()
            active = 1
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineItinerariesAsync)
        } ?: throw SoapError("Server error")
    }.flatMap { itineraryResponse ->
        ItineraryWithStops(
            itineraryCode = itineraryResponse.itineraries?.lineItinerary?.firstOrNull { it.direction == direction }?.codItinerary
                ?: return@flatMap NotFound().left(),
            fullLineCode = lineCode,
            direction = direction - 1,
            stops = itineraryResponse.itineraries.lineItinerary.firstOrNull { it.direction == direction }?.stops?.shortStop?.mapIndexed { index, shortStop ->
                StopOrder(
                    shortStop.codStop,
                    "",
                    index,
                    0
                )
            } ?: listOf(),
            tripId = null
        ).right()
    }.mapLeft(mapExceptionsF)