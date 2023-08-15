package busTrackerApi

import busTrackerApi.config.configureRoutingV1
import busTrackerApi.utils.getenvOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.websocket.*

fun main() {
    embeddedServer(Netty, port = getenvOrNull("PORT")?.toIntOrNull() ?: 8080) {
        startUp()
    }.start(wait = true)
}

fun Application.startUp() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost()
    }
    install(WebSockets)
    configureRoutingV1()
}