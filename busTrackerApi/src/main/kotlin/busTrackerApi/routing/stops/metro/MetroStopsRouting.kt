package busTrackerApi.routing.stops.metro

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

const val metroCodMode = "4"

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
    alertsConfigF(metroCodMode)
}