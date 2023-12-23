package busTrackerApi.routing.lines.bus

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.busLinesRouting() = route("/bus") {
    get("/{lineCode}/locations/{direction}") {
        handle { getLocations() }
    }

    get("/{lineCode}/itineraries/{direction}") {
        handle { getItineraries() }
    }

    get("/shapes/{itineraryCode}") {
        handle { getShapes() }
    }
}

