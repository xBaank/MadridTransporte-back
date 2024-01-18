package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.buildStopTimesJson
import busTrackerApi.routing.stops.trainRouted.trainCodMode
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getTrainStopsTimes() = either {
    val stopCode = call.parameters.getWrapped("stopCode").bind()
    val fullStopCode = createStopCode(trainCodMode, stopCode)
    val times = getTrainTimes(fullStopCode).bind()
    call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
    ResponseJson(buildStopTimesJson(times), HttpStatusCode.OK)
}