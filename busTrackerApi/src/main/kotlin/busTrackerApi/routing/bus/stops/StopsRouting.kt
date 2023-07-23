package busTrackerApi.routing.bus.stops

import busTrackerApi.extensions.handle
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.busStopsRouting() = route("/stops") {
    get("/{stopCode}/estimations") {
        getEstimations().handle()
    }

    get("/{stopCode}/times") {
        getStopTimes().handle()
    }

    get("/{stopCode}/times/cached") {
        getStopTimesCached().handle()
    }

    authenticate("user") {
        webSocket("/{stopCode}/times/subscribe") {
            subscribeStopsTimes().handle()
        }
    }
}

