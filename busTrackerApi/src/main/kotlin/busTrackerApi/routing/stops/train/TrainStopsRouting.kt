package busTrackerApi.routing.stops.train

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val trainCodMode = "5"
fun Route.trainStopsRouting() = route("/train") {

    get("/{stopCode}/times") {
        handle { getTrainTimes() }
    }

    get("/{stopCode}/times/cached") {
        handle { getTrainTimesCached() }
    }

    alertsConfigF(trainCodMode)
}