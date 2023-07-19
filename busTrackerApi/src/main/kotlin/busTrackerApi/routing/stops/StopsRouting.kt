package busTrackerApi.routing.stops

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.routing.*

fun Route.stopsRouting() = route("/stops") {
    get("/query") {
        getStopsByQuery().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}