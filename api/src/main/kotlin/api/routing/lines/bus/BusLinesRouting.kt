package api.routing.lines.bus

import api.extensions.handle
import api.routing.lines.getItinerariesByItineraryCode
import api.routing.lines.getShapes
import io.ktor.server.routing.*

fun Route.busLinesRouting() = route("/bus") {

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

