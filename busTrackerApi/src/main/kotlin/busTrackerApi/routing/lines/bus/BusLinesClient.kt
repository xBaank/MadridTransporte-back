package busTrackerApi.routing.lines.bus

import arrow.core.continuations.either
import busTrackerApi.db.getItineraryByFullLineCode
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.lines.buildVehicleLocationJson
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import crtm.utils.getCodModeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import simpleJson.asJson

suspend fun Pipeline.getLocations() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())
    val stopCode = call.request.queryParameters["stopCode"]

    val fullStopCode = if (stopCode != null) createStopCode(busCodMode, stopCode) else null
    val codMode = getCodModeFromLineCode(lineCode)

    var itinerary = getItineraryByFullLineCode(lineCode, direction - 1)
    //TODO save in db so we don't have to ask the shitty server again
    if (itinerary == null) {
        itinerary = getItinerariesResponse(lineCode).bind().firstOrNull { it.direction == direction - 1 }
            ?: shift<Nothing>(NotFound())
    }

    val locations = getLocationsResponse(itinerary, lineCode, codMode, fullStopCode).bind()

    val json = locations.vehiclesLocation.vehicleLocation
        .map(::buildVehicleLocationJson)
        .asJson()

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    ResponseJson(json, HttpStatusCode.OK)
}