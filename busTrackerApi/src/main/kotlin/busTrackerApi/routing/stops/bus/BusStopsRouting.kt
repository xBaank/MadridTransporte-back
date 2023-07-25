package busTrackerApi.routing.stops.bus

import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val codMode = "8"
fun Route.busStopsRouting() = route("/bus") {
    timesConfigF(codMode)
}

