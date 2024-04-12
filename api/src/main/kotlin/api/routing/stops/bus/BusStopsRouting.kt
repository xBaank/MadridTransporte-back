package api.routing.stops.bus

import api.routing.stops.alertsConfigF
import api.routing.stops.subConfigF
import api.routing.stops.timesConfigF
import api.routing.stops.timesPlannedConfigF
import io.ktor.server.routing.*

const val busCodMode = "8"
const val urbanCodMode = "9"
fun Route.busStopsRouting() = route("/bus") {
    timesConfigF(busCodMode)
    subConfigF(busCodMode)
    alertsConfigF(busCodMode)
    timesPlannedConfigF(busCodMode)
}

