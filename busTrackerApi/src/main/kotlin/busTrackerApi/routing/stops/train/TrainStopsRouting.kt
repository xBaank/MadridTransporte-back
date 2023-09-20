package busTrackerApi.routing.stops.train

import busTrackerApi.extensions.handle
import busTrackerApi.routing.stops.alertsConfigF
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

const val trainCodMode = "5"
fun Route.trainStopsRouting() = route("/train") {

    get("/times") {
        handle {
            getTrainTimes().onRight {
                call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
            }
        }
    }

    alertsConfigF(trainCodMode)
}