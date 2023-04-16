package busTrackerApi.routing.stops

import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.asJson
import simpleJson.serialized

fun Route.stopsRouting() = route("/stops") {
    get("/{stopCode}/times") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
        val codMode = call.request.queryParameters["codMode"]
        val stopTimes = try {
            getStopTimes(stopCode, codMode)
        } catch (e: Exception) {
            null
        }
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = stopTimes.stopTimes.times.shortTime.map(::buildStopTimesJson).asJson()

        call.respondText(json.serialized(), ContentType.Application.Json)
    }
}