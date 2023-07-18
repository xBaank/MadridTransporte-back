package busTrackerApi.routing.bus.stops

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.stopsRouting() = route("/stops") {
    get("/locations") {
        getLocations().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{stopCode}/estimations") {
        getEstimations().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{stopCode}/times") {
        getStopTimes().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{stopCode}/times/cached") {
        getStopTimesCached().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/query") {
        getStopsByQuery().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    authenticate("user") {
        webSocket("/{stopCode}/times/subscribe") {
            subscribeStopsTimes().fold(
                { handleError(it) },
                { }
            )
        }
    }
}

