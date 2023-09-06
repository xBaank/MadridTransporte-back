package busTrackerApi

import arrow.core.getOrElse
import busTrackerApi.config.configureRoutingV1
import busTrackerApi.config.setupFirebase
import busTrackerApi.routing.stops.notifyStopTimesOnBackground
import busTrackerApi.utils.getenvOrNull
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.cors.routing.*


fun main() {
    embeddedServer(CIO, port = getenvOrNull("PORT")?.toIntOrNull() ?: 8080) {
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
    install(CachingHeaders)
    setupFirebase().getOrElse { throw it }
    notifyStopTimesOnBackground()
    configureRoutingV1()
}

