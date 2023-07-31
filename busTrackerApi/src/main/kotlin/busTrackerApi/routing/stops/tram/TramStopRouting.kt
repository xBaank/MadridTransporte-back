package busTrackerApi.routing.stops.tram

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import io.ktor.server.routing.*

const val tramCodMode = "10"

fun Route.tramStopsRouting() = route("/tram") {

    get("/{stopCode}/times") {
        handle { getTramTimes() }
    }

    get("/{stopCode}/times/cached") {
        handle { getTramTimesCached() }
    }

    alertsConfigF(tramCodMode)
}