package utils

import api.startUp
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.testing.*

suspend fun ApplicationTestBuilder.getAbono(id: String) =
    client.get("/abono/$id")

suspend fun ApplicationTestBuilder.getItineraries(line: String, direction: Int) =
    client.get("/lines/bus/$line/itineraries/$direction?stopCode=01231")

suspend fun ApplicationTestBuilder.getShapes(itineraryId: String) =
    client.get("/lines/bus/shapes/$itineraryId")

fun testApplicationBusTracker(
    startUpF: Application.() -> Unit = {
        MongoContainer.start()
        startUp()
    },
    block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit
) = testApplication {
    application {
        startUpF()
    }
    block(client)
}