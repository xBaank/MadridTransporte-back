package busTrackerApi.routing.stops.metro

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.buildStopTimesJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*

suspend fun Call.getMetroTimes(codMode: String) =
    getMetroTimesBase(::getMetroTimesResponse, codMode, call.parameters.getWrapped("stopCode"))

private suspend fun getMetroTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, StopTimes>,
    codMode: String,
    id: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(codMode, id.bind())
    val times = f(stopCode, codMode).bind()
    val statusCode = if (times.arrives == null) HttpStatusCode.BadRequest else HttpStatusCode.OK
    ResponseJson(buildStopTimesJson(times), statusCode)
}


