package busTrackerApi.plugins

import busTrackerApi.routing.lines.linesRouting
import busTrackerApi.routing.stops.stopsRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutingV1() {
    routing {
        route("/v1") {
            linesRouting()
            stopsRouting()
        }
    }
}