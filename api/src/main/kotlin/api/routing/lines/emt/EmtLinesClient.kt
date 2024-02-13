package api.routing.lines.emt

import api.db.getRoute
import api.exceptions.BusTrackerException
import api.exceptions.BusTrackerException.BadRequest
import api.extensions.getWrapped
import api.routing.Response
import api.routing.Response.ResponseJson
import api.routing.lines.VehicleLocations
import api.routing.lines.buildVehicleLocationJson
import api.routing.stops.emt.emtCodMode
import api.utils.Pipeline
import arrow.core.Either
import arrow.core.continuations.either
import crtm.utils.createStopCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getLocations(): Either<BusTrackerException, Response> = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val fullStopCode = createStopCode(emtCodMode, stopCode)
    val route = getRoute(lineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(lineCode)
    val codMode = route?.codMode ?: emtCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(fullStopCode, simpleLineCode).bind()
            .filter { it.direction == direction },
        codMode = codMode.toInt(),
        lineCode = simpleLineCode
    )

    val json = buildVehicleLocationJson(locations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 15))
    ResponseJson(json, HttpStatusCode.OK)
}