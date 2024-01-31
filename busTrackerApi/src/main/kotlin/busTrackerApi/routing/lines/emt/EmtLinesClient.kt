package busTrackerApi.routing.lines.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.db.getItinerariesByItineraryCode
import busTrackerApi.db.getRoute
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.lines.VehicleLocations
import busTrackerApi.routing.lines.buildVehicleLocationJson
import busTrackerApi.routing.stops.emt.emtCodMode
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.flow.firstOrNull

suspend fun Pipeline.getLocationsByItineraryCode(): Either<BusTrackerException, Response> = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()
    val stopCode = call.request.queryParameters["stopCode"]
    val itinerary = getItinerariesByItineraryCode(itineraryCode).firstOrNull() ?: shift<Nothing>(NotFound())


    val fullStopCode = if (stopCode != null) createStopCode(emtCodMode, stopCode) else run {
        itinerary.stops[itinerary.stops.size - 2].fullStopCode
    }

    val route = getRoute(itinerary.fullLineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(itineraryCode)
    val codMode = route?.codMode ?: emtCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(fullStopCode, simpleLineCode).bind()
            .filter { it.direction == itinerary.direction + 1 },
        codMode = codMode.toInt(),
        lineCode = simpleLineCode
    )

    val json = buildVehicleLocationJson(locations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 15))
    ResponseJson(json, HttpStatusCode.OK)
}