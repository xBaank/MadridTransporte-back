package api.routing.lines

import api.extensions.handle
import io.ktor.server.routing.*

val linesConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        get("/shapes/{itineraryCode}") {
            handle { getShapes() }
        }

        get("/{lineCode}/itineraries/{direction}") {
            handle { getItineraries(codMode) }
        }
    }