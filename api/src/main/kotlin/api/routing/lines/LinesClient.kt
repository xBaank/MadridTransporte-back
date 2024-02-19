package api.routing.lines

import api.db.getItinerariesByFullLineCode
import api.db.getRoute
import api.db.getShapesByItineraryCode
import api.exceptions.BusTrackerException.BadRequest
import api.exceptions.BusTrackerException.NotFound
import api.extensions.getWrapped
import api.routing.Response.ResponseFlowJson
import api.routing.Response.ResponseJson
import api.utils.Pipeline
import arrow.core.raise.either
import crtm.utils.createStopCode
import crtm.utils.getCodModeFromLineCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


suspend fun Pipeline.getItineraries(codMode: String) = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: raise(BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val itinerary = getItinerariesByFullLineCode(lineCode, direction, createStopCode(codMode, stopCode)).firstOrNull()
        ?: raise(NotFound())

    val itineraryOrdered =
        itinerary.copy(stops = itinerary.stops.distinctBy { it.fullStopCode to it.order }.sortedBy { it.order })

    val json = itineraryOrdered.let(::buildItineraryJson)
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseJson(json, HttpStatusCode.OK)
}

fun Pipeline.getShapes() = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()

    val shapes = getShapesByItineraryCode(itineraryCode)

    val json = shapes.map(::buildShapeJson)
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseFlowJson(json, HttpStatusCode.OK)
}

suspend fun Pipeline.getLocations() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: raise(BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val codMode = getCodModeFromLineCode(lineCode)
    val fullStopCode = createStopCode(codMode, stopCode)
    val route = getRoute(lineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(lineCode)
    val routeCodMode = route?.codMode ?: codMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(
            lineCode,
            direction,
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