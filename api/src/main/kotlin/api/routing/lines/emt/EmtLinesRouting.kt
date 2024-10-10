package api.routing.lines.emt

import api.routing.handleResponse
import api.routing.lines.linesConfigF
import common.utils.emtCodMode
import io.ktor.server.routing.*

fun Route.emtLinesRouting() = route("/emt") {
    linesConfigF(emtCodMode)

    get("/{lineCode}/locations/{direction}") {
        handleResponse { getLocations() }
    }
}