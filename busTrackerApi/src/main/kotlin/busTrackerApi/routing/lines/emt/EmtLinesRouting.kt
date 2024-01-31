package busTrackerApi.routing.lines.emt

import busTrackerApi.extensions.handle
import busTrackerApi.routing.lines.getItinerariesByItineraryCode
import busTrackerApi.routing.lines.getShapes
import io.ktor.server.routing.*

fun Route.emtLinesRouting() = route("/emt") {

    get("/shapes/{itineraryCode}") {
        handle { getShapes() }
    }

    get("/locations/{itineraryCode}") {
        handle { getLocationsByItineraryCode() }
    }

    get("/itineraries/{itineraryCode}") {
        handle { getItinerariesByItineraryCode() }
    }
}