package busTrackerApi.routing.stops.emt

import arrow.core.getOrElse
import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

const val emtCodMode = "6"
fun Route.emtStopsRouting() = route("/emt") {
    runBlocking { login().getOrElse { throw it } } //Try to log in and throw if it fails

    get("/{stopCode}/times") {
        handle { getStopTimes() }
    }

    get("/{stopCode}/times/cached") {
        handle { getStopTimesCached() }
    }
    subConfigF(emtCodMode) {
        getStopTimesResponse(it).map(TimedCachedValue<StopTimes>::value)
    }

    alertsConfigF(emtCodMode)
}