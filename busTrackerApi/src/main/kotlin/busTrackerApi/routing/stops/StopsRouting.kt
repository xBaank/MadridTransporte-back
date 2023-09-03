package busTrackerApi.routing.stops

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.handle
import io.ktor.server.routing.*

fun Route.stopsRouting() {

    get("/all") {
        handle { getAllStops() }
    }
}

val timesConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/{stopCode}/times") {
        handle { getStopTimes(codMode) }
    }

    get("/{stopCode}/times/cached") {
        handle { getStopTimesCached(codMode) }
    }
}

val subConfigF: Route.(codMode: String, f: suspend (String) -> Either<BusTrackerException, StopTimes>) -> Unit =
    { codMode, f ->
        post("/times/subscribe") {
            handle { subscribeStopTime(codMode, f) }
        }
        post("/times/subscriptions") {
            handle { getSubscriptions(codMode) }
        }
        post("/times/unsubscribe") {
            handle { unsubscribeStopTime(codMode) }
        }
        post("/times/unsubscribe/all") {
            handle { unsubscribeAllStopTime(codMode) }
        }
    }


val alertsConfigF: Route.(codMode: String) -> Unit = { codMode ->
    get("/alerts") {
        handle { getAlertsByCodMode(codMode) }
    }
}