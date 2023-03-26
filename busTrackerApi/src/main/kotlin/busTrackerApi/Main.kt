package busTrackerApi

import busTrackerApi.plugins.configureRoutingV1
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureRoutingV1()
        install(CORS) {
            anyHost()
        }
    }.start(wait = true)
}