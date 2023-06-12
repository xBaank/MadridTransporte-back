package busTrackerApi

import busTrackerApi.config.configureAuth
import busTrackerApi.config.configureDependencies
import busTrackerApi.config.configureRoutingV1
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import java.lang.System.getenv

fun main() {
    embeddedServer(Netty, port = getenv("PORT")?.toIntOrNull() ?: 8080) {
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
    configureDependencies()
    configureAuth()
    configureRoutingV1()
}