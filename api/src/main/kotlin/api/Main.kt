package api

import api.config.EnvVariables
import api.config.configureRoutingV1
import api.config.setupFirebase
import api.config.setupMongo
import api.db.loadDataIntoDb
import api.notifications.notifyStopTimesOnBackground
import arrow.core.getOrElse
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
    loadDataIntoDb()
    configureRoutingV1()
    notifyStopTimesOnBackground()
}

