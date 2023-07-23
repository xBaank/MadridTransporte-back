package busTrackerApi.routing.metro

import busTrackerApi.extensions.handle
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.timesRouting() {
    get("/times") {
        getTimes().handle()
    }
    get("/times/{id}") {
        getTimes(call.parameters["id"]).handle()
    }
}