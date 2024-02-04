package api.routing.stops.metro

import api.db.checkStopExists
import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.routing.stops.buildStopTimesJson
import api.utils.Pipeline
import arrow.core.continuations.either
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


