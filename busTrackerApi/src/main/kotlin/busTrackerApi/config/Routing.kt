package busTrackerApi.config

import busTrackerApi.routing.abono.abonoRouting
import busTrackerApi.routing.bus.lines.linesRouting
import busTrackerApi.routing.favorites.favoritesRouting
import busTrackerApi.routing.metro.timesRouting
import busTrackerApi.routing.stops.bus.busStopsRouting
import busTrackerApi.routing.stops.stopsRouting
import busTrackerApi.routing.stops.train.trainStopsRouting
import busTrackerApi.routing.users.authRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutingV1() {
    routing {
        route("/v1") {
            busRoute()
            metroRoute()
            usersRoute()
            favoriteRoute()
            abonosRoute()
            stopsRoute()
        }
    }
}

private fun Route.stopsRoute() {
    route("/stops") {
        stopsRouting()
        busStopsRouting()
        trainStopsRouting()
    }
}

private fun Route.favoriteRoute() {
    route("/favorites") {
        favoritesRouting()
    }
}

private fun Route.usersRoute() {
    route("/users") {
        authRouting()
    }
}

private fun Route.busRoute() {
    route("/bus") {
        linesRouting()
    }
}

private fun Route.metroRoute() {
    route("/metro") {
        timesRouting()
    }
}

private fun Route.abonosRoute() {
    route("/abono") {
        abonoRouting()
    }
}