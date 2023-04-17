package busTrackerApi.routing.stops

import crtm.utils.createStopCode
import io.github.reactivecircus.cache4k.Cache
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.serialized
import kotlin.time.Duration.Companion.seconds

val stopTimesCache = Cache.Builder().expireAfterWrite(30.seconds).build<String, JsonNode>()

fun Route.stopsRouting() = route("/stops") {
    get("/{stopCode}/times") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
        val codMode = call.request.queryParameters["codMode"]
        val stopTimes = try {
            getStopTimes(stopCode, codMode)
        } catch (e: Exception) {
            null
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = stopTimesCache.get(stopCode + codMode) {
            stopTimes.stopTimes.times.shortTime.map(::buildStopTimesJson).asJson()
        }

        call.respondText(json.serialized(), ContentType.Application.Json)
    }
}