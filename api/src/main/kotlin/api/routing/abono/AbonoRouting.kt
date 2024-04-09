package api.routing.abono

import api.routing.handle
import io.ktor.server.routing.*

fun Route.abonoRouting() {
    get("/{id}") {
        handle { getAbono() }
    }
    post("/subscribe") {
        handle { subscribeAbono() }
    }
    post("/unsubscribe") {
        handle { unsubscribeAbono() }
    }
    post("/subscription") {
        handle { abonoSubscription() }
    }
    post("/subscriptions") {
        handle { abonoSubscriptions() }
    }
}