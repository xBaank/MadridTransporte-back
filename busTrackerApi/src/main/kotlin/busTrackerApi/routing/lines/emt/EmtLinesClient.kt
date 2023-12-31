package busTrackerApi.routing.lines.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.lines.buildVehicleLocationJson
import busTrackerApi.routing.stops.emt.emtCodMode
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import crtm.utils.getSimpleLineCodeFromLineCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.asJson

suspend fun Pipeline.getLocations(): Either<BusTrackerException, Response> = either {
    val lineCode = call.parameters.getWrapped("lineCode").bind()
    val direction =
        call.parameters.getWrapped("direction").bind().toIntOrNull() ?: shift<Nothing>(BusTrackerException.BadRequest())
    val stopCode = call.request.queryParameters.getWrapped("stopCode").bind()

    val fullStopCode = createStopCode(emtCodMode, stopCode)

    val locations = getLocationsResponse(fullStopCode, getSimpleLineCodeFromLineCode(lineCode)).bind()
        .filter { it.direction == direction }
        .map(::buildVehicleLocationJson).asJson()

    ResponseJson(locations, HttpStatusCode.OK)
}