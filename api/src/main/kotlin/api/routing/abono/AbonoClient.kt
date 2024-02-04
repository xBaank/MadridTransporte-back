package api.routing.abono

import api.extensions.getWrapped
import api.routing.Response.ResponseJson
import api.utils.Pipeline
import arrow.core.continuations.either
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