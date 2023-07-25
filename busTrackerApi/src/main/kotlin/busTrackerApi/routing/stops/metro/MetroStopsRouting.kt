package busTrackerApi.routing.stops.metro

import busTrackerApi.extensions.handle
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.metroStopsRouting() = route("/metro") {
    get("/{stopCode}/times") {
        handle { getMetroTimes() }
    }
    get("/{stopCode}/times/cached") {
        handle { getMetroTimesCached() }
    }

    authenticate("user") {
        webSocket("/{stopCode}/times/subscribe") {
            handle { subscribeMetroStopsTimes() }
        }
    }
}