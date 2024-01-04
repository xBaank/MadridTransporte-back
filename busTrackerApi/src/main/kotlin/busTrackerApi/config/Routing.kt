package busTrackerApi.config

import busTrackerApi.routing.abono.abonoRouting
import busTrackerApi.routing.lines.bus.busLinesRouting
import busTrackerApi.routing.lines.emt.emtLinesRouting
import busTrackerApi.routing.stops.bus.busStopsRouting
import busTrackerApi.routing.stops.emt.emtStopsRouting
import busTrackerApi.routing.stops.metro.metroStopsRouting
import busTrackerApi.routing.stops.metro.tramStopsRouting
import busTrackerApi.routing.stops.stopsRouting
import busTrackerApi.routing.stops.train.trainStopsRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutingV1() {
    routing {
        linesRoute()
        abonosRoute()
        stopsRoute()
    }
}

private fun Route.stopsRoute() {
    route("/stops") {
        stopsRouting()
        busStopsRouting()
        trainStopsRouting()
        metroStopsRouting()
        tramStopsRouting()
        emtStopsRouting()
    }
}

private fun Route.linesRoute() {
    route("/lines") {
        busLinesRouting()
        emtLinesRouting()
    }
}

private fun Route.abonosRoute() {
    route("/abono") {
        abonoRouting()
    }
}