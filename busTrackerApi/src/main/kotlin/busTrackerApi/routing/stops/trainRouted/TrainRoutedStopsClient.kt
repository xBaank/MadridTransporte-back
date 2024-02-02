package busTrackerApi.routing.stops.trainRouted

import arrow.core.continuations.either
import busTrackerApi.db.getIdByStopCode
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Pipeline
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

suspend fun Pipeline.getTrainRoutedTimes() = either {
    val originId = call.request.queryParameters.getWrapped("originStopCode")
    val destinationId = call.request.queryParameters.getWrapped("destinationStopCode")
    val stopCodeOrigin = createStopCode(trainCodMode, originId.bind())
    val stopCodeDestination = createStopCode(trainCodMode, destinationId.bind())
    val stopInfoOriginStationCode = getIdByStopCode(stopCodeOrigin).bind()
    val stopInfoDestinationStationCode = getIdByStopCode(stopCodeDestination).bind()
    val json = getTrainRoutedTimesResponse(
        stopInfoOriginStationCode,
        stopInfoDestinationStationCode
    ).bind()
    call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
    ResponseJson(json, HttpStatusCode.OK)
}