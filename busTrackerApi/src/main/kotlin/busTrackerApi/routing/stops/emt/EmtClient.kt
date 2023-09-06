package busTrackerApi.routing.stops.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.buildStopTimesJson
import busTrackerApi.routing.stops.checkStopExists
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*

suspend fun Call.getStopTimes() = getStopTimesBase(::getEmtStopTimesResponse, call.parameters.getWrapped("stopCode"))

private suspend fun getStopTimesBase(
    f: suspend (String) -> Either<BusTrackerException, StopTimes>,
    simpleStopCode: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(emtCodMode, simpleStopCode.bind())
    checkStopExists(stopCode).bind()
    val times = f(stopCode).bind()
    ResponseJson(buildStopTimesJson(times), HttpStatusCode.OK)
}