package api.routing.stops.metro

import api.routing.handle
import api.routing.stops.*
import io.ktor.server.routing.*

const val metroCodMode = "4"
const val tramCodMode = "10"

fun Route.metroStopsRouting() = route("/metro") {
    get("/{stopCode}/times") {
        handle { getStopTimesResponse(::getMetroTimes, metroCodMode, 10) }
    }
    subConfigF(metroCodMode)
    alertsConfigF(metroCodMode)
    timesPlannedConfigF(metroCodMode)
}

fun Route.tramStopsRouting() = route("/tram") {
    timesConfigF(tramCodMode) //Metro doesn't work with tram
    subConfigF(tramCodMode)
    alertsConfigF(tramCodMode)
    timesPlannedConfigF(tramCodMode)
}