package busTrackerApi.routing.stops

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.stopsRouting() {
    get("/search") {
        getStopsByQuery().handle()
    }

    get("/locations") {
        getLocations().handle()
    }
}