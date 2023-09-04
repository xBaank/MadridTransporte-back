package busTrackerApi.routing.stops

import busTrackerApi.extensions.handle
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/all") {
        call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
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

val subConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        post("/times/subscribe") {
            handle { subscribeStopTime(codMode) }
        }
        post("/times/subscriptions") {
            handle { getSubscriptions(codMode) }
        }
        post("/times/unsubscribe") {
            handle { unsubscribeStopTime(codMode) }
        }
        post("/times/unsubscribe/all") {
            handle { unsubscribeAllStopTime(codMode) }
        }
    }


val alertsConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/alerts") {
        handle { getAlertsByCodMode(codMode) }
    }
}