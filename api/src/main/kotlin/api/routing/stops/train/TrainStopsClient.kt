package api.routing.stops.train

import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.routing.stops.buildStopTimesJson
import api.routing.stops.trainRouted.trainCodMode
import api.utils.Pipeline
import arrow.core.continuations.either
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getTrainStopsTimesResponse() = either {
    val stopCode = call.parameters.getWrapped("stopCode").bind()
    val fullStopCode = createStopCode(trainCodMode, stopCode)
    val times = getTrainStopTimes(fullStopCode).bind()
    call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
    ResponseJson(buildStopTimesJson(times), HttpStatusCode.OK)
}