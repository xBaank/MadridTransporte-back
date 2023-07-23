package busTrackerApi.routing.bus.lines

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.linesRouting() = route("/lines") {
    get("/{lineCode}/locations") {
       getLocationsHandler().handle()
    }

    get("/{lineCode}/stops") {
        getStopsHandler().handle()
    }

    get("/{lineCode}/itineraries") {
        getItinerariesHandler().handle()
    }
}

