package busTrackerApi.routing.stops.metro

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import busTrackerApi.routing.stops.subConfigF
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

const val metroCodMode = "4"
const val tramCodMode = "10"

fun Route.metroStopsRouting() = route("/metro") {
    metroConfigF(metroCodMode)
    subConfigF(metroCodMode)
}

fun Route.tramStopsRouting() = route("/tram") {
    metroConfigF(tramCodMode)
    subConfigF(tramCodMode)
}

private val metroConfigF: Route.(String) -> Unit = { codMode ->
    get("/{stopCode}/times") {
        call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
        handle { getMetroTimes(codMode) }
    }
    alertsConfigF(codMode)
}