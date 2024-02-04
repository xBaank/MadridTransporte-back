package api.routing.lines.bus

import api.db.getItinerariesByItineraryCode
import api.db.getRoute
import api.exceptions.BusTrackerException.NotFound
import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.routing.lines.VehicleLocations
import api.routing.lines.buildVehicleLocationJson
import api.routing.stops.bus.busCodMode
import api.utils.Pipeline
import arrow.core.continuations.either
import crtm.utils.createStopCode
import crtm.utils.getCodModeFromLineCode
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

    val fullStopCode = if (stopCode != null) createStopCode(busCodMode, stopCode) else null
    val codMode = getCodModeFromLineCode(itinerary.fullLineCode)

    val route = getRoute(itinerary.fullLineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(itineraryCode)
    val routeCodMode = route?.codMode ?: busCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(
            itinerary,
            itinerary.fullLineCode,
            codMode,
            fullStopCode
        ).bind().vehiclesLocation.vehicleLocation,
        codMode = routeCodMode.toInt(),
        lineCode = simpleLineCode,
    )

    val json = buildVehicleLocationJson(locations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    ResponseJson(json, HttpStatusCode.OK)
}