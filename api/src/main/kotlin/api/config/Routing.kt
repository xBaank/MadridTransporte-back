package api.config

import api.routing.abono.abonoRouting
import api.routing.lines.bus.busLinesRouting
import api.routing.lines.emt.emtLinesRouting
import api.routing.lines.linesRouting
import api.routing.stops.bus.busStopsRouting
import api.routing.stops.emt.emtStopsRouting
import api.routing.stops.metro.metroStopsRouting
import api.routing.stops.metro.tramStopsRouting
import api.routing.stops.stopsRouting
import api.routing.stops.train.trainStopsRouting
import api.routing.stops.trainRouted.trainRoutedStopsRouting
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
        trainRoutedStopsRouting()
        trainStopsRouting()
        metroStopsRouting()
        tramStopsRouting()
        emtStopsRouting()
    }
}

private fun Route.linesRoute() {
    route("/lines") {
        linesRouting()
        busLinesRouting()
        emtLinesRouting()
    }
}

private fun Route.abonosRoute() {
    route("/abono") {
        abonoRouting()
    }
}