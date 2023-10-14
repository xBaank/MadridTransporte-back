package utils

import busTrackerApi.startUp
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.testing.*


suspend fun ApplicationTestBuilder.getAbono(id: String) =
    client.get("/v1/abono/$id")

suspend fun ApplicationTestBuilder.getLineLocation(line: String) =
    client.get("/v1/bus/lines/$line/locations")

suspend fun ApplicationTestBuilder.getItineraries(line: String) =
    client.get("/v1/bus/lines/$line/itineraries")

suspend fun ApplicationTestBuilder.getStops(line: String) =
    client.get("/v1/bus/lines/$line/stops")

fun testApplicationBusTracker(
    startUpF: Application.() -> Unit = {
        MongoContainer.start()
        startUp()
    },
    block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit
) = testApplication {
    val newClient = createClient {
        install(WebSockets) {
            pingInterval = 1000
        }
    }
    application {
        startUpF()
    }
    block(newClient)
}