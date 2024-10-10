package api.routing.lines

import api.routing.handleResponse
import arrow.core.right
import io.ktor.server.routing.*

val linesConfigF: Route.(codMode: String) -> Unit =
    { codMode ->
        get("/shapes/{itineraryCode}") {
            handleResponse { getShapes() }
        }

        get("/itineraries/{itineraryCode}/locations") {
            handleResponse { getLocationsByItineraryCode(codMode) }
        }

        get("/{lineCode}/itineraries/{direction}") {
            handleResponse { getItineraries(codMode) }
        }

        get("/itineraries/{itineraryCode}") {
            handleResponse { getItinerariesByCode() }
        }
    }

fun Route.linesRouting() {
    get("/all") {
        handleResponse { getAllLinesRoutes().right() }
    }
}