package busTrackerApi.routing.bus.lines

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.getCodModeFromLineCode
import crtm.utils.isValidLineCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.asJson

suspend fun Call.getLocationsHandler() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val codMode = getCodModeFromLineCode(lineCode)
    if(!isValidLineCode(lineCode)) shift<Nothing>(BadRequest("Line code is not valid"))

    val locations = getItineraries(lineCode).bind()
        .itineraries
        .lineItinerary
        .map { getLocations(it, lineCode, codMode).bind() }

    val json = locations.mapNotNull { it?.vehiclesLocation?.vehicleLocation?.map(::buildVehicleLocationJson) }
        .flatten()
        .asJson()

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getStopsHandler() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val codMode = getCodModeFromLineCode(lineCode)
    if(!isValidLineCode(lineCode)) shift<Nothing>(BadRequest("Line code is not valid"))

    val stops = getStops(lineCode, codMode).bind()
    val json = stops.stops.stop.map(::buildStopsJson).asJson()

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getItinerariesHandler() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    if(!isValidLineCode(lineCode)) shift<Nothing>(BadRequest("Line code is not valid"))

    val itineraries = getItineraries(lineCode).bind()
    val json = itineraries.itineraries.lineItinerary.map(::buildItinerariesJson).asJson()

    ResponseJson(json, HttpStatusCode.OK)
}