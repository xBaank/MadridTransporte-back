package api.routing.lines.bus

import api.routing.lines.linesConfigF
import api.routing.stops.bus.busCodMode
import io.ktor.server.routing.*

fun Route.busLinesRouting() = route("/bus") {
    linesConfigF(busCodMode)
}

