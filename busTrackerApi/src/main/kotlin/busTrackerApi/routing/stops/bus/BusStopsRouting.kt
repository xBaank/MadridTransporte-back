package busTrackerApi.routing.stops.bus

import busTrackerApi.routing.stops.*
import io.ktor.server.routing.*

const val busCodMode = "8"
fun Route.busStopsRouting() = route("/bus") {
    timesConfigF(busCodMode)
    subConfigF(busCodMode) { getTimesResponse(it).map(TimedCachedValue<StopTimes>::value) }
    alertsConfigF(busCodMode)
}

