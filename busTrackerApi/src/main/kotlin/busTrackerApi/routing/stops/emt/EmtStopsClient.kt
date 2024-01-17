package busTrackerApi.routing.stops.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.db.checkStopExists
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.buildStopTimesJson
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getStopTimes() =
    getStopTimesBase(::getEmtStopTimes, call.parameters.getWrapped("stopCode"))

private suspend fun Pipeline.getStopTimesBase(
    f: suspend (String) -> Either<BusTrackerException, StopTimes>,
    simpleStopCode: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(emtCodMode, simpleStopCode.bind())
    checkStopExists(stopCode).bind()
    val times = f(stopCode).bind()
    if (times.arrives != null) call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    val statusCode = if (times.arrives == null) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK
    ResponseJson(buildStopTimesJson(times), statusCode)
}