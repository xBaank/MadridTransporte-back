package busTrackerApi.routing.stops.`train-routed`

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import io.ktor.server.routing.*

const val trainCodMode = "5"
fun Route.trainStopsRouting() = route("/train") {

    get("/times") {
        handle { getTrainTimes() }
    }

    alertsConfigF(trainCodMode)
}