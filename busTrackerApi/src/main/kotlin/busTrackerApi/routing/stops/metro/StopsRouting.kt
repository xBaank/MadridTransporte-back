package busTrackerApi.routing.stops.metro

import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val codMode = "4"
fun Route.metroStopsRouting() = route("/metro") {
    timesConfigF(codMode)
}