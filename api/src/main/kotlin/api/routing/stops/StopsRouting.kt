package api.routing.stops

import api.routing.handleResponse
import api.routing.stops.bus.getCRTMStopTimes
import arrow.core.right
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/all") {
        handleResponse { getAllStops().right() }
    }
    post("/times/subscriptions") {
        handleResponse { stopTimesSubscriptions() }
    }
}

val timesConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        get("/{stopCode}/times") {
            handleResponse { getStopTimesResponse(::getCRTMStopTimes, codMode, 15) }
        }
    }

val timesPlannedConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        get("/{stopCode}/planned") {
            handleResponse { getTimesPlanned(codMode) }
        }
    }

val subConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        post("/times/subscribe") {
            handleResponse { subscribeStopTime(codMode) }
        }
        post("/times/subscription") {
            handleResponse { stopTimesSubscription(codMode) }
        }
        post("/times/unsubscribe") {
            handleResponse { unsubscribeStopTime(codMode) }
        }
    }


val alertsConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/alerts") {
        handleResponse { getAlertsByCodMode(codMode) }
    }
}