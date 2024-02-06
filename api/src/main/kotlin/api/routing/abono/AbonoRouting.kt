package api.routing.abono

import api.extensions.handle
import io.ktor.server.routing.*

fun Route.abonoRouting() {
    get("{id}") {
        handle { getAbono() }
    }
    post("/abono/subscribe") {
        handle { subscribeAbono() }
    }
    post("/abono/unsubscribe") {
        handle { unsubscribeAbono() }
    }
    post("/abono/subscription") {
        handle { subscriptionAbono() }
    }
}