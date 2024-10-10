package utils

import api.startUp
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.testing.*

fun testApplicationBusTracker(
    startUpF: Application.() -> Unit = {
        MongoContainer.start()
        startUp()
    },
    block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit,
) = testApplication {
    application {
        startUpF()
    }
    block(client)
}