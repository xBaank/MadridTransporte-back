package busTrackerApi.routing.stops

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/search") {
        getStopsByQuery().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/locations") {
        getLocations().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}