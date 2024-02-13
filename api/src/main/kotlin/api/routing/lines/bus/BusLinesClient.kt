package api.routing.lines.bus

import api.db.getRoute
import api.exceptions.BusTrackerException.BadRequest
import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.routing.lines.VehicleLocations
import api.routing.lines.buildVehicleLocationJson
import api.routing.stops.bus.busCodMode
import api.utils.Pipeline
import arrow.core.continuations.either
import crtm.utils.createStopCode
import crtm.utils.getCodModeFromLineCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getLocations() = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction = call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val fullStopCode = createStopCode(busCodMode, stopCode)
    val codMode = getCodModeFromLineCode(lineCode)
    val route = getRoute(lineCode).getOrNull()
    val simpleLineCode = route?.simpleLineCode ?: getSimpleLineCodeFromLineCode(lineCode)
    val routeCodMode = route?.codMode ?: busCodMode

    val locations = VehicleLocations(
        locations = getLocationsResponse(
            lineCode,
            direction,
            codMode,
            fullStopCode
        ).bind().vehiclesLocation.vehicleLocation,
        codMode = routeCodMode.toInt(),
        lineCode = simpleLineCode,
    )

    val json = buildVehicleLocationJson(locations)

    call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    ResponseJson(json, HttpStatusCode.OK)
}