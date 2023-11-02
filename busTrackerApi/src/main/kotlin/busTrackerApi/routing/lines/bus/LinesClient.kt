package busTrackerApi.routing.lines.bus

import arrow.core.continuations.either
import busTrackerApi.db.getItinerariesByFullLineCode
import busTrackerApi.db.getItineraryByFullLineCode
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.mapAsync
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import crtm.utils.getCodModeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import simpleJson.asJson

suspend fun Call.getLocations() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())
    val stopCode = call.request.queryParameters["stopCode"]

    val fullStopCode = if (stopCode != null) createStopCode(busCodMode, stopCode) else null
    val codMode = getCodModeFromLineCode(lineCode)

    var itineraries = getItinerariesByFullLineCode(lineCode, direction - 1)
    if (itineraries.isEmpty()) itineraries =
        getItinerariesResponse(lineCode).bind().filter { it.direction == direction - 1 }

    val locations = itineraries.mapAsync {
        getLocationsResponse(it, lineCode, codMode, fullStopCode).bind()
    }

    val json = locations
        .map { it.vehiclesLocation.vehicleLocation }
        .flatten()
        .map(::buildVehicleLocationJson)
        .asJson()

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getItineraries() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())


    val itineraries =
        getItineraryByFullLineCode(lineCode, direction - 1)
            ?: getItinerariesResponse(lineCode).bind()
                .sortedBy { it.stops.count() }
                .firstOrNull { it.direction == direction - 1 }
            ?: shift<Nothing>(NotFound("Itinerary not found"))

    val json = itineraries.let(::buildItinerariesJson).asJson()

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60))
    ResponseJson(json, HttpStatusCode.OK)
}