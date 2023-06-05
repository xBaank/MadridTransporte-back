package busTrackerApi

import busTrackerApi.plugins.configureAuth
import busTrackerApi.plugins.configureDependencies
import busTrackerApi.plugins.configureRoutingV1
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import java.lang.System.getenv

fun main() {
    embeddedServer(Netty, port = getenv("PORT")?.toIntOrNull() ?: 8080) {
        configureRoutingV1()
        configureAuth()
        configureDependencies()
        install(CORS) {
            anyHost()
        }
    }.start(wait = true)
}