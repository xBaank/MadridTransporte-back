package api.routing.abono

import api.extensions.handle
import io.ktor.server.routing.*

fun Route.abonoRouting() {
    get("{id}") {
        handle { getAbono() }
    }
}