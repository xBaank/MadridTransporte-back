package api.routing.stops

import api.extensions.handle
import arrow.core.right
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/all") {
        handle { getAllStops().right() }
    }
    post("/times/subscriptions") {
        handle { stopTimesSubscriptions() }
    }
}

val subConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        post("/times/subscribe") {
            handle { subscribeStopTime(codMode) }
        }
        post("/times/subscription") {
            handle { stopTimesSubscription(codMode) }
        }
        post("/times/unsubscribe") {
            handle { unsubscribeStopTime(codMode) }
        }
    }


val alertsConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/alerts") {
        handle { getAlertsByCodMode(codMode) }
    }
}