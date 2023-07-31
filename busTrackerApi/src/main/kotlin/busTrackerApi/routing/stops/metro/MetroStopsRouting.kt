package busTrackerApi.routing.stops.metro

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import io.ktor.server.routing.*

const val metroCodMode = "4"

fun Route.metroStopsRouting() = route("/metro") {
    get("/{stopCode}/times") {
        handle { getMetroTimes() }
    }

    get("/{stopCode}/times/cached") {
        handle { getMetroTimesCached() }
    }

    alertsConfigF(metroCodMode)
}