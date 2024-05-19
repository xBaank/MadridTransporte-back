package api.routing.lines

import api.extensions.getWrapped
import api.routing.Response.ResponseFlowJson
import api.routing.Response.ResponseJson
import api.utils.Pipeline
import arrow.core.raise.either
import common.exceptions.BusTrackerException.BadRequest
import common.exceptions.BusTrackerException.NotFound
import common.queries.*
import common.utils.createStopCode
import common.utils.getCodModeFromLineCode
import common.utils.getSimpleLineCodeFromLineCode
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

suspend fun Pipeline.getItinerariesByCode() = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()

    val itinerary = getItineraryByCode(itineraryCode) ?: raise(NotFound("Itinerary code not found"))

    val itineraryOrdered =
        itinerary.copy(stops = itinerary.stops.distinctBy { it.fullStopCode to it.order }.sortedBy { it.order })

    val json = itineraryOrdered.let(::buildItineraryJson)
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseJson(json, HttpStatusCode.OK)
}

fun Pipeline.getAllLinesRoutes(): ResponseFlowJson {
    val routes = getRoutesWithItineraries()
    val json = routes.map(::buildRouteJson)
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    return ResponseFlowJson(json, HttpStatusCode.OK)
}

fun Pipeline.getShapes() = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()

    val shapes = getShapesByItineraryCode(itineraryCode)

    val json = shapes.map(::buildShapeJson)
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseFlowJson(json, HttpStatusCode.OK)
}

suspend fun Pipeline.getLocations(codMode: String) = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: raise(BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val lineCodMode = getCodModeFromLineCode(lineCode)
    val fullStopCode = createStopCode(codMode, stopCode)
    val route = getRoute(lineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(lineCode)
    val routeCodMode = route?.codMode ?: lineCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(
            lineCode,
            direction,
            lineCodMode,
            fullStopCode
        ).bind().vehiclesLocation.vehicleLocation,
        codMode = routeCodMode.toInt(),
        lineCode = simpleLineCode,
    )

    val json = buildVehicleLocationJson(locations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Pipeline.getLocationsByItineraryCode(codMode: String) = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val lineCode = getItineraryByCode(itineraryCode)?.fullLineCode ?: raise(NotFound("Itinerary code not found"))
    val lineCodMode = getCodModeFromLineCode(lineCode)
    val fullStopCode = createStopCode(codMode, stopCode)
    val route = getRoute(lineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(lineCode)
    val routeCodMode = route?.codMode ?: lineCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(
            lineCode,
            itineraryCode,
            lineCodMode,
            fullStopCode
        ).bind().vehiclesLocation.vehicleLocation,
        codMode = routeCodMode.toInt(),
        lineCode = simpleLineCode,
    )

    val json = buildVehicleLocationJson(locations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    ResponseJson(json, HttpStatusCode.OK)
}