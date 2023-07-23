package busTrackerApi.routing.abono

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.abonoRouting() {
    get("{id}") {
        getAbono().handle()
    }
}