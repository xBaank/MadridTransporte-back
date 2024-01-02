package busTrackerApi.routing.stops.metro

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

suspend fun Pipeline.getMetroTimes(codMode: String) =
    getMetroTimesBase(::getMetroTimesResponse, codMode, call.parameters.getWrapped("stopCode"))

private suspend fun Pipeline.getMetroTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, StopTimes>,
    codMode: String,
    id: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(codMode, id.bind())
    checkStopExists(stopCode).bind()
    val times = f(stopCode, codMode).bind()
    if (times.arrives != null) call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    val statusCode = if (times.arrives == null) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK
    ResponseJson(buildStopTimesJson(times), statusCode)
}


