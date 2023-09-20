package busTrackerApi.routing.abono

import busTrackerApi.extensions.handle
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

fun Route.abonoRouting() {
    get("{id}") {
        handle {
            getAbono().onRight {
                call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
            }
        }
    }
}