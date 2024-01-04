package busTrackerApi.routing.lines

import arrow.core.continuations.either
import busTrackerApi.db.getItineraryByFullLineCode
import busTrackerApi.db.getShapesByItineraryCode
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseFlowJson
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.lines.bus.getItinerariesResponse
import busTrackerApi.utils.Pipeline
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.flow.map
import simpleJson.asJson

suspend fun Pipeline.getItineraries() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull()
        ?: shift<Nothing>(busTrackerApi.exceptions.BusTrackerException.BadRequest())

    val itineraries = getItineraryByFullLineCode(lineCode, direction - 1)
        ?: getItinerariesResponse(lineCode).bind()
            .sortedBy { it.stops.count() }
            .firstOrNull { it.direction == direction - 1 }
        ?: shift<Nothing>(busTrackerApi.exceptions.BusTrackerException.NotFound("Itinerary not found"))

    val itinerariesOrdered =
        itineraries.copy(stops = itineraries.stops.distinctBy { it.fullStopCode to it.order }.sortedBy { it.order })

    val json = itinerariesOrdered.let(::buildItineraryJson).asJson()
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Pipeline.getShapes() = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()

    val shapes = getShapesByItineraryCode(itineraryCode)

    val json = shapes.map(::buildShapeJson)
    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
    ResponseFlowJson(json, HttpStatusCode.OK)
}