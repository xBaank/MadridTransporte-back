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
) {
    //The start method loads all data into the db and takes around 5 minutes
    //The testApplication body have a test timeout of 1 minute, so we have to run it outside
    MongoContainer.start()
    testApplication {
        application {
            startUpF()
        }
        block(client)
    }
}