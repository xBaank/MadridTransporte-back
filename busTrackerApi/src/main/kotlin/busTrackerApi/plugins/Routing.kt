package busTrackerApi.plugins

import busTrackerApi.routing.bus.lines.linesRouting
import busTrackerApi.routing.bus.stops.stopsRouting
import busTrackerApi.routing.favourites.favouritesRouting
import busTrackerApi.routing.metro.timesRouting
import busTrackerApi.routing.users.authRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutingV1() {
    routing {
        route("/v1") {
            busRouting()
            metroRouting()
            usersRouting()
            favoriteRouting()
        }
    }
}

private fun Route.favoriteRouting() {
    route("/favorites") {
        favouritesRouting()
    }
}

private fun Route.usersRouting() {
    route("/users") {
        authRouting()
    }
}

private fun Route.busRouting() {
    route("/bus") {
        linesRouting()
        stopsRouting()
    }
}

private fun Route.metroRouting() {
    route("/metro") {
        timesRouting()
    }
}