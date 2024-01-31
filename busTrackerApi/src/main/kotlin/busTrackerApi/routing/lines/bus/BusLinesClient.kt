package busTrackerApi.routing.lines.bus

import arrow.core.continuations.either
import busTrackerApi.db.getItinerariesByItineraryCode
import busTrackerApi.db.getItineraryByFullLineCode
import busTrackerApi.db.getRoute
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.lines.VehicleLocations
import busTrackerApi.routing.lines.buildVehicleLocationJson
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import crtm.utils.getCodModeFromLineCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.flow.firstOrNull

suspend fun Pipeline.getLocations() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())
    val stopCode = call.request.queryParameters["stopCode"]

    val fullStopCode = if (stopCode != null) createStopCode(busCodMode, stopCode) else null
    val codMode = getCodModeFromLineCode(lineCode)

    val itinerary = getItineraryByFullLineCode(lineCode, direction) ?: shift<Nothing>(NotFound())

    val route = getRoute(lineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(lineCode)
    val routeCodMode = route?.codMode ?: busCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(
            itinerary,
            lineCode,
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