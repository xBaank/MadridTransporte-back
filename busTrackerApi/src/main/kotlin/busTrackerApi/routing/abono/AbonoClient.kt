package busTrackerApi.routing.abono

import arrow.core.continuations.either
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Pipeline
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*


suspend fun Pipeline.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val result = getAbonoResponse(id).bind()
    call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}