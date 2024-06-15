package api.routing.lines.metro

import api.routing.lines.linesConfigF
import common.utils.metroCodMode
import common.utils.tramCodMode
import io.ktor.server.routing.*

fun Route.metroLinesRouting() = route("/metro") {
    linesConfigF(metroCodMode)
}

fun Route.tramLinesRouting() = route("/tram") {
    linesConfigF(tramCodMode)
}