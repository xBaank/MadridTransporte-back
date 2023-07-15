package busTrackerApi.routing.bus.stops

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import busTrackerApi.utils.errorObject
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import simpleJson.jObject
import simpleJson.serialized
import kotlin.time.Duration.Companion.minutes

val subscribedStops = mutableMapOf<String, DefaultWebSocketSession>()

fun Route.stopsRouting() = route("/stops") {
    get("/locations") {
        getLocations().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{stopCode}/estimations") {
        getEstimations().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{stopCode}/times") {
        getStopTimes().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{stopCode}/times/cached") {
        getStopTimesCached().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    authenticate("user") {
        webSocket("/{stopCode}/times/subscribe") {
            val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
            val codMode = call.request.queryParameters["codMode"]
            val ip = call.request.origin.remoteAddress
            val email = call.principal<JWTPrincipal>()?.get("email")
            val subId = "$ip-$email-$stopCode"
            if (subscribedStops.containsKey(subId)) {
                return@webSocket close(
                    CloseReason(
                        CloseReason.Codes.VIOLATED_POLICY,
                        errorObject("Already subscribed to stop code $stopCode").serialized()
                    )
                )
            }
            try {
                subscribedStops[subId] = this
                while (isActive) {
                    val timedVCached = getTimesOrCached(stopCode, codMode)
                        ?: return@webSocket close(
                            CloseReason(
                                CloseReason.Codes.INTERNAL_ERROR,
                                errorObject("No stop times found for stop code $stopCode").serialized()
                            )
                        )

                    val json = jObject {
                        "data" += timedVCached.value
                        "lastTime" += timedVCached.createdAt.toEpochMilli()
                    }

                    send(json.serialized())
                    delay(1.minutes)
                }
            } finally {
                subscribedStops.remove(subId)
            }
        }
    }
}

