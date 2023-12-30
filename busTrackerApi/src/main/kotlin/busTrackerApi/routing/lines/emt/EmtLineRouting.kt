package busTrackerApi.routing.lines.emt

import busTrackerApi.extensions.handle
import busTrackerApi.routing.lines.bus.getItineraries
import busTrackerApi.routing.lines.bus.getLocations
import busTrackerApi.routing.lines.bus.getShapes
import io.ktor.server.routing.*

fun Route.emtLinesRouting() = route("/emt") {
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