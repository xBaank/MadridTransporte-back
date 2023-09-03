package busTrackerApi.routing.stops

import busTrackerApi.extensions.handle
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/all") {
        handle { getAllStops() }
    }
}

val timesConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/{stopCode}/times") {
        call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
        handle { getStopTimes(codMode) }
    }

    get("/{stopCode}/times/cached") {
        handle { getStopTimesCached(codMode) }
    }
}

val alertsConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/alerts") {
        handle { getAlertsByCodMode(codMode) }
    }
}