package busTrackerApi.routing.stops.trainRouted

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

const val trainCodMode = "5"
fun Route.trainRoutedStopsRouting() = route("/train") {
    get("/times") {
        handle { getTrainRoutedTimes() }
    }
}