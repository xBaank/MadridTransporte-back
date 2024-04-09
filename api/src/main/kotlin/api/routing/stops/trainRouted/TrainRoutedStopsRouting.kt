package api.routing.stops.trainRouted

import api.routing.handle
import io.ktor.server.routing.*

const val trainCodMode = "5"
fun Route.trainRoutedStopsRouting() = route("/train") {
    get("/times") {
        handle { getTrainRoutedTimes() }
    }
}