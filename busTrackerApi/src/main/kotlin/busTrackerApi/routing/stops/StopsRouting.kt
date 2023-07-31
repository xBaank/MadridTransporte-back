package busTrackerApi.routing.stops

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/search") {
        handle { getStopsByQuery() }
    }

    get("/locations") {
        handle { getStopsByLocation() }
    }

    get("/all") {
        handle { getAllStops() }
    }
}

val timesConfigF : Route.(codMode : String) -> Unit = { codMode ->
    get("/{stopCode}/times") {
        handle { getStopTimes(codMode) }
    }

    get("/{stopCode}/times/cached") {
        handle { getStopTimesCached(codMode) }
    }
}

val alertsConfigF : Route.(codMode : String) -> Unit = { codMode ->
    get("/alerts") {
        handle { getAlertsByCodMode(codMode) }
    }
}