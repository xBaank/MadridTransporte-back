package api.routing.stops.metro

import api.extensions.handle
import api.routing.stops.alertsConfigF
import api.routing.stops.subConfigF
import api.routing.stops.timesConfigF
import io.ktor.server.routing.*

const val metroCodMode = "4"
const val tramCodMode = "10"

fun Route.metroStopsRouting() = route("/metro") {
    metroConfigF(metroCodMode)
    subConfigF(metroCodMode)
}

fun Route.tramStopsRouting() = route("/tram") {
    timesConfigF(tramCodMode)
    subConfigF(tramCodMode)
}

private val metroConfigF: Route.(String) -> Unit = { codMode ->
    get("/{stopCode}/times") {
        handle { getMetroTimesResponse(codMode) }
    }
    alertsConfigF(codMode)
}