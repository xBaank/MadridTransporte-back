package api.routing.stops.emt

import api.extensions.handle
import api.routing.stops.alertsConfigF
import api.routing.stops.subConfigF
import arrow.core.getOrElse
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

const val emtCodMode = "6"
fun Route.emtStopsRouting() = route("/emt") {
    runBlocking { login().getOrElse { throw it } } //Try to log in and throw if it fails

    get("/{stopCode}/times") {
        handle { getEMTStopTimesResponse() }
    }

    subConfigF(emtCodMode)
    alertsConfigF(emtCodMode)
}