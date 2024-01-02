package busTrackerApi.routing.lines.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.db.getItineraryByFullLineCode
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.BadRequest
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.lines.buildVehicleLocationJson
import busTrackerApi.routing.stops.emt.emtCodMode
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import simpleJson.asJson

suspend fun Pipeline.getLocations(): Either<BusTrackerException, Response> = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction =
        call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())
    val stopCode = call.request.queryParameters["stopCode"]

    val fullStopCode = if (stopCode != null) createStopCode(emtCodMode, stopCode) else run {
        val itinerary = getItineraryByFullLineCode(lineCode, direction - 1) ?: shift<Nothing>(NotFound())
        itinerary.stops[itinerary.stops.size - 2].fullStopCode
    }

    val locations =
        getLocationsResponse(fullStopCode, getSimpleLineCodeFromLineCode(lineCode)).bind()
            .filter { it.direction == direction }
            .map(::buildVehicleLocationJson).asJson()

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 15))
    ResponseJson(locations, HttpStatusCode.OK)
}