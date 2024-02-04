package api.routing.stops.bus

import api.extensions.handle
import api.routing.stops.alertsConfigF
import api.routing.stops.subConfigF
import io.ktor.server.routing.*

const val busCodMode = "8"
fun Route.busStopsRouting() = route("/bus") {
    get("/{stopCode}/times") {
        handle { getBusStopTimesResponse() }
    }
    subConfigF(busCodMode)
    alertsConfigF(busCodMode)
}

