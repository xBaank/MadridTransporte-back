package busTrackerApi.routing.stops.bus

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.subConfigF
import io.ktor.server.routing.*

const val busCodMode = "8"
fun Route.busStopsRouting() = route("/bus") {
    get("/{stopCode}/times") {
        handle { getBusStopTimesResponse() }
    }
    subConfigF(busCodMode)
    alertsConfigF(busCodMode)
}

