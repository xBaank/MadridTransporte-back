package busTrackerApi.routing.bus.stops

import busTrackerApi.errorObject
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
        val latitude = call.request.queryParameters["latitude"]?.toDoubleOrNull() ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            errorObject("Missing latitude")
        )
        val longitude = call.request.queryParameters["longitude"]?.toDoubleOrNull() ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            errorObject("Missing longitude")
        )

        return@get getStopsByLocation(lat = latitude, lon = longitude)
            .fold(
                ifLeft = { call.respond(HttpStatusCode.NotFound) },
                ifRight = { call.respondText(buildStopLocationsJson(it).serialized(), ContentType.Application.Json) }
            )
    }

    get("/{stopCode}/estimations") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)

        val estimationsVCached = getEstimations(stopCode).getOrNull()
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = jObject {
            "data" += estimationsVCached.value
            "lastTime" += estimationsVCached.createdAt.toEpochMilli()
        }

        call.respondText(json.serialized(), ContentType.Application.Json)
    }

    get("/{stopCode}/times") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
        val codMode = call.request.queryParameters["codMode"]

        val timedVCached = tryGetTimesOrCached(stopCode, codMode)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = jObject {
            "data" += timedVCached.value
            "lastTime" += timedVCached.createdAt.toEpochMilli()
        }

        call.respondText(json.serialized(), ContentType.Application.Json)
    }

    get("/{stopCode}/times/cached") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
        val cached = stopTimesCache.get(stopCode)
        if (cached == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        val json = jObject {
            "data" += cached.value
            "lastTime" += cached.createdAt.toEpochMilli()
        }
        call.respondText(json.serialized(), ContentType.Application.Json)
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
                        errorObject("Already subscribed to stop code $stopCode")
                    )
                )
            }
            try {
                subscribedStops[subId] = this
                while (isActive) {
                    val timedVCached = tryGetTimesOrCached(stopCode, codMode)
                        ?: return@webSocket close(
                            CloseReason(
                                CloseReason.Codes.INTERNAL_ERROR,
                                errorObject("No stop times found for stop code $stopCode")
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

