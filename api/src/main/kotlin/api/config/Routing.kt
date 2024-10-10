package api.config

import api.routing.Response.ResponseJson
import api.routing.handleResponse
import api.routing.lines.bus.busLinesRouting
import api.routing.lines.emt.emtLinesRouting
import api.routing.lines.linesRouting
import api.routing.lines.metro.metroLinesRouting
import api.routing.lines.metro.tramLinesRouting
import api.routing.lines.train.trainLinesRouting
import api.routing.stops.bus.busStopsRouting
import api.routing.stops.emt.emtStopsRouting
import api.routing.stops.metro.metroStopsRouting
import api.routing.stops.metro.tramStopsRouting
import api.routing.stops.stopsRouting
import api.routing.stops.train.trainStopsRouting
import api.routing.stops.trainRouted.trainRoutedStopsRouting
import arrow.core.right
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import simpleJson.jObject

fun Application.configureRouting() {
    routing {
        healthCheck()
        linesRoute()
        stopsRoute()
    }
}

private fun Route.healthCheck() = get("/health") {
    handleResponse { ResponseJson(jObject { "isRunning" += true }, HttpStatusCode.OK).right() }
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
        metroLinesRouting()
        trainLinesRouting()
        tramLinesRouting()
    }
}