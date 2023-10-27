package busTrackerApi.routing.bus.lines

import arrow.core.continuations.either
import busTrackerApi.db.getItinerariesByFullLineCode
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.mapAsync
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.getCodModeFromLineCode
import crtm.utils.isValidLineCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.asJson

suspend fun Call.getLocations() = either {

    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction =
        call.request.queryParameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())

    val codMode = getCodModeFromLineCode(lineCode)

    val itineraries = getItinerariesByFullLineCode(lineCode, direction - 1)
    if (itineraries.isEmpty()) shift<Nothing>(NotFound())

    val locations = itineraries.mapAsync {
        getLocationsResponse(it, lineCode, codMode).bind()
    }

    val json = locations
        .map { it.vehiclesLocation.vehicleLocation }
        .flatten()
        .map(::buildVehicleLocationJson)
        .asJson()

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getStops() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val codMode = getCodModeFromLineCode(lineCode)
    if (!isValidLineCode(lineCode)) shift<Nothing>(BadRequest("Line code is not valid"))

    val stops = getStopsResponse(lineCode, codMode).bind()
    val json = stops.stops.stop.map(::buildStopsJson).asJson()

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getItineraries() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    if (!isValidLineCode(lineCode)) shift<Nothing>(BadRequest("Line code is not valid"))

    val itineraries = getItinerariesResponse(lineCode).bind()
    val json = itineraries.itineraries.lineItinerary.map(::buildItinerariesJson).asJson()

    ResponseJson(json, HttpStatusCode.OK)
}