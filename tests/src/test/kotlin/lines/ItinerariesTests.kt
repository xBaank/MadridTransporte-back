package lines

import arrow.core.raise.either
import common.extensions.getOrThrow
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.*
import utils.testApplicationBusTracker


enum class ItinerariesUrls(val url: String, val direction: Int) {
    EmtDirectionBased("/lines/emt/6__144___/itineraries/2?stopCode=4597", 2),
    InterurbanDirectionBased("/lines/bus/8__450___/itineraries/1?stopCode=08242", 1),
    UrbanDirectionBased("/lines/bus/9__2__065_/itineraries/2?stopCode=08242", 2),
    MetroDirectionBased("/lines/metro/4__12___/itineraries/1?stopCode=205", 1),
    TrainDirectionBased("/lines/train/5__4_A__/itineraries/1?stopCode=53", 1),
    TramDirection("/lines/tram/10__ML4___/itineraries/1?stopCode=64", 1),

    EmtCodeBased("/lines/emt/itineraries/6__144____2__IT_1", 2),
    InterurbanCodeBased("/lines/bus/itineraries/8__450____1_-_IT_1", 1),
    UrbanCodeBased("/lines/bus/itineraries/9__2__065__2_-_IT_1", 2),
    MetroCodeBased("/lines/metro/itineraries/4__12_2___1__IT_1", 2),
    TrainCodeBased("/lines/train/itineraries/340742", 1),
    TramBased("/lines/tram/itineraries/10__4_1___1__IT_1", 1)
}

class ItinerariesTests {
    @ParameterizedTest
    @EnumSource(ItinerariesUrls::class)
    fun `should get itineraries from line`(code: ItinerariesUrls) = testApplicationBusTracker {
        val response = client.get(code.url)
        val json = response.bodyAsText().deserialized()


        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json["codItinerary"].asString().bind()
            json["direction"].asInt().bind().shouldBeEqualTo(code.direction)
            json["stops"].asArray().bind().shouldNotBeEmpty()
            json["stops"].asArray().bind().forEach {
                it["fullStopCode"].asString().bind()
                it["order"].asInt().bind().shouldBeGreaterOrEqualTo(0)
            }
        }.getOrThrow()
    }

    @Test
    fun `should not get itineraries from line`() = testApplicationBusTracker {
        val response = client.get("/lines/bus/asd/itineraries/1?stopCode=01231")
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}