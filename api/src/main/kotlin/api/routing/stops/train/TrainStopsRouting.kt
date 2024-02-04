package api.routing.stops.train

import api.extensions.handle
import api.routing.stops.alertsConfigF
import api.routing.stops.subConfigF
import api.routing.stops.trainRouted.trainCodMode
import io.ktor.server.routing.*

fun Route.trainStopsRouting() = route("/train") {
    get("/{stopCode}/times") {
        handle { getTrainStopsTimesResponse() }
    }

    subConfigF(trainCodMode)
    alertsConfigF(trainCodMode)
}