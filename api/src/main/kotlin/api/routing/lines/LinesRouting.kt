package api.routing.lines

import api.routing.handle
import arrow.core.right
import io.ktor.server.routing.*

val linesConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        get("/shapes/{itineraryCode}") {
            handle { getShapes() }
        }

        get("/{lineCode}/locations/{direction}") {
            handle { getLocations(codMode) }
        }

        get("/{lineCode}/itineraries/{itineraryCode}/locations") {
            handle { getLocationsByItineraryCode(codMode) }
        }

        get("/{lineCode}/itineraries/{direction}") {
            handle { getItineraries(codMode) }
        }
    }

fun Route.linesRouting() {
    get("/routes") {
        handle { getAllLinesRoutes().right() }
    }
}