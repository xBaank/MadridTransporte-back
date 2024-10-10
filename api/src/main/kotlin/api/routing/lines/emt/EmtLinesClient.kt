package api.routing.lines.emt

import api.extensions.getWrapped
import api.routing.Response
import api.routing.Response.ResponseJson
import api.routing.lines.buildVehicleLocationJson
import api.utils.Pipeline
import arrow.core.Either
import arrow.core.raise.either
import common.exceptions.BusTrackerException
import common.utils.createStopCode
import common.utils.emtCodMode
import common.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getLocations(): Either<BusTrackerException, Response> = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction =
        call.parameters.getWrapped("direction").bind().toIntOrNull() ?: raise(BusTrackerException.BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val fullStopCode = createStopCode(emtCodMode, stopCode)

    val locations = getLocationsResponse(fullStopCode, getSimpleLineCodeFromLineCode(lineCode)).bind()
    val mappedLocations = locations.copy(locations = locations.locations.filter { it.direction == direction })
    val json = buildVehicleLocationJson(mappedLocations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 15))
    ResponseJson(json, HttpStatusCode.OK)
}