package api

import api.config.EnvVariables
import api.config.configureRoutingV1
import api.config.setupFirebase
import api.notifications.notifyStopTimesOnBackground
import common.DB
import common.extensions.getOrThrow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.runBlocking


fun main() {
    embeddedServer(Netty, port = EnvVariables.port) {
        startUp()
        //startup is used on tests and notifications tests are mocked, so we don't include those methods in the startup
        notifyStopTimesOnBackground()
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
    install(Compression) {
        gzip()
        deflate()
    }
    install(CachingHeaders)
    DB.setupMongo(EnvVariables.mongoConnectionString.getOrThrow())
    setupFirebase().getOrThrow()
    configureRoutingV1()
}

