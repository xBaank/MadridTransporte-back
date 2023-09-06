package busTrackerApi.routing.stops.emt

import arrow.core.getOrElse
import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.subConfigF
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

const val emtCodMode = "6"
fun Route.emtStopsRouting() = route("/emt") {
    runBlocking { login().getOrElse { throw it } } //Try to log in and throw if it fails

    get("/{stopCode}/times") {
        call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
        handle { getStopTimes() }
    }

    subConfigF(emtCodMode)
    alertsConfigF(emtCodMode)
}