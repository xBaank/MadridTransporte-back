package api.routing.stops.train

import api.routing.handleResponse
import api.routing.stops.alertsConfigF
import api.routing.stops.getStopTimesResponse
import api.routing.stops.subConfigF
import common.utils.trainCodMode
import io.ktor.server.routing.*

fun Route.trainStopsRouting() = route("/train") {
    get("/{stopCode}/times") {
        handleResponse { getStopTimesResponse(::getTrainTimes, trainCodMode, 10) }
    }

    subConfigF(trainCodMode)
    alertsConfigF(trainCodMode)
}