package busTrackerApi.routing.stops.emt

import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

//TODO Doesnt work, we need to use the official EMT API and maybe correlation as in metro
const val codMode = "6"
fun Route.emtStopsRouting() = route("/emt") {
    timesConfigF(codMode)
}

