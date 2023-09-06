package busTrackerApi.routing.stops.bus

import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.subConfigF
import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val busCodMode = "8"
fun Route.busStopsRouting() = route("/bus") {
    timesConfigF(busCodMode)
    subConfigF(busCodMode)
    alertsConfigF(busCodMode)
}

