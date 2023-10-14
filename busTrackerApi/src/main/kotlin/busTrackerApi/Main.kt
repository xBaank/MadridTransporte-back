package busTrackerApi

import arrow.core.getOrElse
import busTrackerApi.config.EnvVariables
import busTrackerApi.config.configureRoutingV1
import busTrackerApi.config.setupFirebase
import busTrackerApi.config.setupMongo
import busTrackerApi.db.loadDataIntoDb
import busTrackerApi.routing.stops.notifyStopTimesOnBackground
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.runBlocking


fun main() {
    embeddedServer(Netty, port = EnvVariables.port) {
        startUp()
    }.start(wait = true)
}

fun Application.startUp() = runBlocking {
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
    setupMongo().getOrElse { throw it }
    loadDataIntoDb().getOrElse { throw it }
    notifyStopTimesOnBackground()
    configureRoutingV1()
}

