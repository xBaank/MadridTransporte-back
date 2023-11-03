package busTrackerApi.routing.lines.bus

import arrow.core.Either
import busTrackerApi.db.Itinerary
import busTrackerApi.db.StopOrder
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import busTrackerApi.utils.timeoutSeconds
import crtm.soap.LineItineraryRequest
import crtm.soap.LineLocationRequest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

val locationsTimeout = 10.seconds
suspend fun getLocationsResponse(itinerary: Itinerary, lineCode: String, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            codItinerary = itinerary.itineraryCode
            direction = itinerary.direction + 1
            authentication = defaultClient.value().auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(locationsTimeout) {
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
            ?: throw BusTrackerException.NotFound("No locations found for line $lineCode")
    } ?: throw SoapError("Server error")

}
    .mapLeft(mapExceptionsF)
    .map { itinerary ->
        itinerary.itineraries.lineItinerary.map {
            Itinerary(
                fullLineCode = lineCode,
                direction = it.direction - 1,
                itineraryCode = it.codItinerary,
                stops = it.stops.shortStop.mapIndexed { index, stop ->
                    StopOrder(stop.codStop, index)
                }
            )
        }
    }