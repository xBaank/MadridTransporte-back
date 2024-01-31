package busTrackerApi.routing.lines.bus

import busTrackerApi.extensions.handle
import busTrackerApi.routing.lines.getItineraries
import busTrackerApi.routing.lines.getItinerariesByItineraryCode
import busTrackerApi.routing.lines.getShapes
import io.ktor.server.routing.*

fun Route.busLinesRouting() = route("/bus") {
    //TODO Deprecate lineCode and direction endpoint on next major release
    get("/{lineCode}/locations/{direction}") {
        handle { getLocations() }
    }

    get("/{lineCode}/itineraries/{direction}") {
        handle { getItineraries() }
    }

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

