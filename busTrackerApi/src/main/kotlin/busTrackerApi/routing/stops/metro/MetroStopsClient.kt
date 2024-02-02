package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import busTrackerApi.db.checkStopExists
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.buildStopTimesJson
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*


suspend fun Pipeline.getMetroTimesResponse(codMode: String) = either {
    val stopCode = call.parameters.getWrapped("stopCode")
    val fullStopCode = createStopCode(codMode, stopCode.bind())
    checkStopExists(fullStopCode).bind()
    val times = getMetroTimes(fullStopCode, codMode).bind()
    if (times.arrives != null) call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
    val statusCode = if (times.arrives == null) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK
    ResponseJson(buildStopTimesJson(times), statusCode)
}


