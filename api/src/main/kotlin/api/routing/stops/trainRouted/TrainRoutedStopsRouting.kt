package api.routing.stops.trainRouted

import api.routing.handleResponse
import io.ktor.server.routing.*

fun Route.trainRoutedStopsRouting() = route("/train") {
    get("/times") {
        handleResponse { getTrainRoutedTimes() }
    }
}