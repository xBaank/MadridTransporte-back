package busTrackerApi.routing.bus.lines

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.routing.*

fun Route.linesRouting() = route("/lines") {
    get("/{lineCode}/locations") {
       getLocationsHandler().fold(
           { handleError(it) },
           { handleResponse(it) }
       )
    }

    get("/{lineCode}/stops") {
        getStopsHandler().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{lineCode}/itineraries") {
        getItinerariesHandler().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}

