package busTrackerApi.routing.stops

import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.asJson
import simpleJson.serialize

fun Route.stopsRouting() = route("/stops") {
    get("/{stopCode}/mode/{codMode}/times") {
        val codMode = call.parameters["codMode"]!!
        val stopCode = createStopCode(codMode,call.parameters["stopCode"]!!)
        val stopTimes = getStopTimes(stopCode,codMode)
        ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = stopTimes.stopTimes.times.time.map(::buildStopTimesJson).asJson()

        call.respondText(json.serialize(), ContentType.Application.Json)
    }
}