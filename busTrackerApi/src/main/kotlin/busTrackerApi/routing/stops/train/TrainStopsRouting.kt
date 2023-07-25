package busTrackerApi.routing.stops.train

import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val codMode = "5"
fun Route.trainStopsRouting() = route("/train") {
    timesConfigF(codMode)
}