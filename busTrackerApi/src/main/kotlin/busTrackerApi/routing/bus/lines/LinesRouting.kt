package busTrackerApi.routing.bus.lines

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.linesRouting() = route("/lines") {
    get("/{lineCode}/locations") {
        handle { getLocations() }
    }

    get("/{lineCode}/stops") {
        handle { getStops() }
    }

    get("/{lineCode}/itineraries") {
        handle { getItineraries() }
    }
}

