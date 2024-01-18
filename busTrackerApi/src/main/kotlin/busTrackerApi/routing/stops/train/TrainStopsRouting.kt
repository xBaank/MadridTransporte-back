package busTrackerApi.routing.stops.train

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.subConfigF
import busTrackerApi.routing.stops.trainRouted.trainCodMode
import io.ktor.server.routing.*

fun Route.trainStopsRouting() = route("/train") {
    get("/{stopCode}/times") {
        handle { getTrainStopsTimes() }
    }

    subConfigF(trainCodMode)
    alertsConfigF(trainCodMode)
}