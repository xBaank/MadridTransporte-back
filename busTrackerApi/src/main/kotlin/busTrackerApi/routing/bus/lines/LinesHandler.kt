package busTrackerApi.routing.bus.lines

import arrow.core.continuations.either
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.getCodModeFromLineCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.asJson

//TODO Apply regex validation to lineCode
suspend fun Call.getLocationsHandler() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val codMode = getCodModeFromLineCode(lineCode)

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

    val stops = getStops(lineCode, codMode).bind()
    val json = stops.stops.stop.map(::buildStopsJson).asJson()

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getItinerariesHandler() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()

    val itineraries = getItineraries(lineCode).bind()
    val json = itineraries.itineraries.lineItinerary.map(::buildItinerariesJson).asJson()

    ResponseJson(json, HttpStatusCode.OK)
}