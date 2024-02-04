package api.routing.lines.emt

import api.db.getItinerariesByItineraryCode
import api.db.getRoute
import api.exceptions.BusTrackerException.NotFound
import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.routing.lines.VehicleLocations
import api.routing.lines.buildVehicleLocationJson
import api.routing.stops.emt.emtCodMode
import api.utils.Pipeline
import arrow.core.continuations.either
import crtm.utils.createStopCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.flow.firstOrNull

suspend fun Pipeline.getLocationsByItineraryCode() = either {
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