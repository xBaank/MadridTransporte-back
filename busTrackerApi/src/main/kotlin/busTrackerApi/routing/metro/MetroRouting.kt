package busTrackerApi.routing.metro

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.timesRouting() {
    get("/times") {
        getTimes().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
    get("/times/{id}") {
        getTimes(call.parameters["id"]).fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}