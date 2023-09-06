package utils

import arrow.core.continuations.either
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Coordinates
import busTrackerApi.routing.stops.Incident
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.startUp
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import simpleJson.*


suspend fun ApplicationTestBuilder.getAbono(id: String) =
    client.get("/v1/abono/$id")

suspend fun ApplicationTestBuilder.getLineLocation(line: String) =
    client.get("/v1/bus/lines/$line/locations")

suspend fun ApplicationTestBuilder.getItineraries(line: String) =
    client.get("/v1/bus/lines/$line/itineraries")

suspend fun ApplicationTestBuilder.getStops(line: String) =
    client.get("/v1/bus/lines/$line/stops")

fun testApplicationBusTracker(
    startUpF: Application.() -> Unit = { startUp() },
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

suspend fun buildStopTimesFromJson(json: JsonNode) = either {
    StopTimes(
        codMode = json["codMode"].asInt().bind(),
        stopName = json["stopName"].asString().bind(),
        simpleStopCode = json["simpleStopCode"].asString().bind(),
        stopCode = json["stopCode"].asString().bind(),
        coordinates = Coordinates(
            latitude = json["coordinates"]["latitude"].asDouble().bind(),
            longitude = json["coordinates"]["longitude"].asDouble().bind()
        ),
        arrives = json["arrives"].asArray().bind().map {
            Arrive(
                codMode = it["codMode"].asInt().bind(),
                line = it["line"].asString().bind(),
                anden = it["anden"].asInt().getOrNull(),
                destination = it["destination"].asString().bind(),
                estimatedArrive = it["estimatedArrives"].asArray().bind().map { it.asLong().bind() }.first()
            )
        },
        incidents = json["incidents"].asArray().bind().map {
            Incident(
                title = it["title"].asString().bind(),
                description = it["description"].asString().bind(),
                from = it["from"].asString().bind(),
                to = it["to"].asString().bind(),
                cause = it["cause"].asString().bind(),
                effect = it["effect"].asString().bind(),
                url = it["url"].asString().bind()
            )
        }
    )
}