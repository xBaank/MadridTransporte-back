package busTrackerApi.routing.stops.train

import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val trainCodMode = "5"
fun Route.trainStopsRouting() = route("/train") {
    timesConfigF(trainCodMode)
    alertsConfigF(trainCodMode)
}