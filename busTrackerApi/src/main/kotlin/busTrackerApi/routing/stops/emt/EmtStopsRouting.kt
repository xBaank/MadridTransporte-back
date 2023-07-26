package busTrackerApi.routing.stops.emt

import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

//TODO Doesnt work, we need to use the official EMT API and maybe correlation as in metro
const val emtCodMode = "6"
fun Route.emtStopsRouting() = route("/emt") {
    timesConfigF(emtCodMode)
    alertsConfigF(emtCodMode)
}