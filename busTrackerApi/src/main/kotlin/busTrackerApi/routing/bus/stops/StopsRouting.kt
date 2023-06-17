package busTrackerApi.routing.bus.stops

import arrow.core.getOrElse
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.jObject
import simpleJson.serialized


fun Route.stopsRouting() = route("/stops") {
    get("/{stopCode}/times") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
        val codMode = call.request.queryParameters["codMode"]

        val timedVCached = tryGetTimes(stopCode, codMode)
            .onRight { stopTimesCache.put(stopCode, it) }
            .getOrElse { stopTimesCache.get(stopCode) }
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
}

