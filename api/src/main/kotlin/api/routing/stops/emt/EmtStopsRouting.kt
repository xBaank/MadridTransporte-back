package api.routing.stops.emt

import api.routing.handle
import api.routing.stops.alertsConfigF
import api.routing.stops.getStopTimesResponse
import api.routing.stops.subConfigF
import arrow.core.getOrElse
import common.utils.emtCodMode
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking

private val logger = KtorSimpleLogger("EmtRoutingLogger")
fun Route.emtStopsRouting() = route("/emt") {
    runBlocking { login().getOrElse(logger::error) } //Try to log in and log if it fails

    get("/{stopCode}/times") {
        handle { getStopTimesResponse(::getEmtStopTimes, emtCodMode, 10) }
    }

    subConfigF(emtCodMode)
    alertsConfigF(emtCodMode)
}