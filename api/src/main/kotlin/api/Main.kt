package api

import api.config.EnvVariables
import api.config.configureRoutingV1
import api.config.setupFirebase
import api.notifications.notifyStopTimesOnBackground
import common.DB
import common.extensions.getOrThrow
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import kotlinx.coroutines.runBlocking


fun main() {
    embeddedServer(Netty, port = EnvVariables.port) {
        startUp()
        //startup is used on tests and notifications tests are mocked, so we don't include those methods in the startup
        notifyStopTimesOnBackground()
    }.start(wait = true)
}

fun Application.startUp() = runBlocking {
    install(Compression) {
        gzip()
        deflate()
    }
    install(CachingHeaders)
    DB.setupMongo(EnvVariables.mongoConnectionString.getOrThrow())
    setupFirebase().getOrThrow()
    configureRoutingV1()
}

