package utils

import api.startUp
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.testing.*

fun testApplicationBusTracker(
    startUpF: Application.() -> Unit = {
        startUp()
    },
    block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit,
) = testApplication {
    MongoContainer.start()
    application {
        startUpF()
    }
    block(client)
}