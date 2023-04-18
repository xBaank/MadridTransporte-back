package busTrackerApi.routing.stops

import busTrackerApi.TimedCachedValue
import busTrackerApi.timed
import crtm.utils.createStopCode
import io.github.reactivecircus.cache4k.Cache
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import simpleJson.JsonNode
import simpleJson.asJson
import simpleJson.jObject
import simpleJson.serialized
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

val stopTimesCache = Cache.Builder().expireAfterWrite(6.hours).build<String, TimedCachedValue<JsonNode>>()

fun Route.stopsRouting() = route("/stops") {
    get("/{stopCode}/times") {
        val stopCode = createStopCode("8", call.parameters["stopCode"]!!)
        val codMode = call.request.queryParameters["codMode"]
        val timedVCached = try {
            withTimeout(20.seconds) {
                val stopTimes = getStopTimes(stopCode, codMode)
                stopTimes?.stopTimes?.times?.shortTime?.map(::buildStopTimesJson)?.asJson()?.timed()
            } ?: stopTimesCache.get(stopCode)
        } catch (e: Exception) {
            if (e is TimeoutCancellationException) stopTimesCache.get(stopCode)
            else null
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        stopTimesCache.put(stopCode, timedVCached)

        val json = jObject {
            "data" += timedVCached.value
            "lastTime" += timedVCached.createdAt.toEpochMilli()
        }
        call.respondText(json.serialized(), ContentType.Application.Json)
    }
}