package api.routing.lines.emt

import api.extensions.handle
import api.routing.lines.linesConfigF
import api.routing.stops.emt.emtCodMode
import io.ktor.server.routing.*

fun Route.emtLinesRouting() = route("/emt") {
    linesConfigF(emtCodMode)
    get("/{lineCode}/locations/{direction}") {
        handle { getLocations() }
    }
}