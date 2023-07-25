package busTrackerApi.routing.metro

import busTrackerApi.extensions.handle
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.timesRouting() {
    get("/times") {
        handle { getTimes() }
    }
    get("/times/{id}") {
        handle { getTimes(call.parameters["id"]) }
    }
}