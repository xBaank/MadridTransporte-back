package busTrackerApi.routing.stops

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

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

    webSocket("/{stopCode}/times/subscribe") {
        handle { subscribeStopsTimes(codMode) }
    }
}

val alertsConfigF : Route.(codMode : String) -> Unit = { codMode ->
    get("/alerts") {
        handle { getAlertsByCodMode(codMode) }
    }
}