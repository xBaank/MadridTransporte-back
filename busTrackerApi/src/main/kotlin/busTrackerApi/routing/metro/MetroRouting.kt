package busTrackerApi.routing.metro

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.serialized

fun Route.timesRouting() {
    get("/times") {
        val result = getTimes() ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respondText(result.serialized(), ContentType.Application.Json)
    }
    get("/times/{id}") {
        val result = getTimes(call.parameters["id"]) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respondText(result.serialized(), ContentType.Application.Json)
    }
}