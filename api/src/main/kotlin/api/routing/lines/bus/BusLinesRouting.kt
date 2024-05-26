package api.routing.lines.bus

import api.routing.handle
import api.routing.lines.getLocations
import api.routing.lines.linesConfigF
import common.utils.busCodMode
import io.ktor.server.routing.*

fun Route.busLinesRouting() = route("/bus") {
    linesConfigF(busCodMode)

    get("/{lineCode}/locations/{direction}") {
        handle { getLocations(busCodMode) }
    }
}

