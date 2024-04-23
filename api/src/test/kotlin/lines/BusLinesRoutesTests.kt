package lines

import api.extensions.getOrThrow
import arrow.core.raise.either
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.junit.jupiter.api.Test
import simpleJson.*
import utils.testApplicationBusTracker

class BusLinesRoutesTests {
    @Test
    fun `should get all routes`() = testApplicationBusTracker {
        val result = client.get("/lines/all")
        val body = result.bodyAsText().deserialized().asArray().getOrThrow()

        either {
            body.forEach {
                it["fullLineCode"].asString().bind()
                it["simpleLineCode"].asString().bind()
                it["codMode"].asInt().bind()
                it["routeName"].asString().bind()
                it["itineraries"].asArray().bind().forEach { itinerary ->
                    itinerary["itineraryCode"].asString().bind()
                    itinerary["direction"].asInt().bind()
                    itinerary["tripName"].asString().bind()
                    itinerary["serviceId"].asString().bind()
                }
            }
        }.getOrThrow()
    }
}