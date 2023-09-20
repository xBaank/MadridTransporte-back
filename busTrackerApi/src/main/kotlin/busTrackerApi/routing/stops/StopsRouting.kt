package busTrackerApi.routing.stops

import busTrackerApi.extensions.handle
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/all") {
        handle { getAllStops().onRight { call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60)) } }
    }
    post("/times/subscriptions") {
        handle { getAllSubscriptions() }
    }
}

val timesConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/{stopCode}/times") {
        handle { getStopTimes(codMode) }
    }
}

val subConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        post("/times/subscribe") {
            handle { subscribeStopTime(codMode) }
        }
        post("/times/subscription") {
            handle { getSubscription(codMode) }
        }
        post("/times/unsubscribe") {
            handle { unsubscribeStopTime(codMode) }
        }
    }


val alertsConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/alerts") {
        handle {
            getAlertsByCodMode(codMode).onRight {
                call.caching = CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60 * 60))
            }
        }
    }
}