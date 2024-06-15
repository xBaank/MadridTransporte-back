package lines

import arrow.core.raise.either
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.*
import utils.testApplicationBusTracker

enum class ItinerariesCodes(val url: String) {
    Emt("/lines/emt/shapes/6__144____1__IT_1"),
    Interurban("/lines/bus/shapes/8__450____1_-_IT_1"),
    Urban("/lines/bus/shapes/9__2__065__2_-_IT_1"),
    Metro("/lines/metro/shapes/4__12_1___1__IT_1"),
    Tram("/lines/tram/shapes/10__4_1___1__IT_1"),
}

class BusShapesTests {
    @ParameterizedTest
    @EnumSource(ItinerariesCodes::class)
    fun `should get bus shapes`(code: ItinerariesCodes) = testApplicationBusTracker {
        val response = client.get(code.url)
        val json = response.bodyAsText().deserialized().asArray()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json.bind().shouldNotBeEmpty()
            json.bind().forEach {
                it["sequence"].asInt().bind()
                it["longitude"].asDouble().bind()
                it["latitude"].asDouble().bind()
                it["distance"].asDouble().bind()
            }
        }
    }

    @Test
    fun `should not get bus shapes`() = testApplicationBusTracker {
        val response = client.get("/lines/bus/shapes/asd")
        val json = response.bodyAsText().deserialized().asArray()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json.bind().shouldBeEmpty()
        }
    }
}