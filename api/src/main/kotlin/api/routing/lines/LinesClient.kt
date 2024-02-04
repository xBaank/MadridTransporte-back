package api.routing.lines

import api.db.getItinerariesByItineraryCode
import api.db.getShapesByItineraryCode
import api.exceptions.BusTrackerException.NotFound
import api.extensions.getWrapped
import api.routing.Response.ResponseFlowJson
import api.routing.Response.ResponseJson
import api.utils.Pipeline
import arrow.core.continuations.either
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import simpleJson.asJson


suspend fun Pipeline.getItinerariesByItineraryCode() = either {
    val itineraryCode = call.parameters.getWrapped("itineraryCode").bind()

    val itineraries =
        getItinerariesByItineraryCode(itineraryCode).firstOrNull() ?: shift<Nothing>(NotFound("Itinerary not found"))

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