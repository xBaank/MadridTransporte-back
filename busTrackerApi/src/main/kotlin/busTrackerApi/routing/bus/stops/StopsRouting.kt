package busTrackerApi.routing.bus.stops

import busTrackerApi.TimedCachedValue
import busTrackerApi.timed
import crtm.utils.createStopCode
import io.github.reactivecircus.cache4k.Cache
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
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
                val stopTimes = CoroutineScope(Dispatchers.IO).async { getStopTimes(stopCode, codMode) }.await()
                stopTimes?.stopTimes?.times?.shortTime?.map(::buildStopTimesJson)?.asJson()?.timed()
            } ?: stopTimesCache.get(stopCode)
        }
        catch (e: Exception) {
            if (e is TimeoutCancellationException) stopTimesCache.get(stopCode)
            else null
        } ?: return@get call.respond(HttpStatusCode.BadRequest)

        stopTimesCache.put(stopCode, timedVCached)

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