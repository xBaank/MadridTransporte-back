package api.routing.stops.train

import api.routing.handle
import api.routing.stops.alertsConfigF
import api.routing.stops.getStopTimesResponse
import api.routing.stops.subConfigF
import api.routing.stops.trainRouted.trainCodMode
import io.ktor.server.routing.*

fun Route.trainStopsRouting() = route("/train") {
    get("/{stopCode}/times") {
        handle { getStopTimesResponse(::getTrainTimes, trainCodMode, 10) }
    }

    subConfigF(trainCodMode)
    alertsConfigF(trainCodMode)
}