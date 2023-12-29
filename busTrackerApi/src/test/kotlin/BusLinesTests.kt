import arrow.core.continuations.either
import busTrackerApi.extensions.getOrThrow
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
import utils.getItineraries
import utils.getLineLocation
import utils.testApplicationBusTracker

enum class LineCodes(val code: String, val direction: Int) {
    Interurban("8__450___", 1),
    Urban("9__1__074_", 1),
    WeirdUrban("9__1__058_", 1)
}

class BusLinesTests {
    @ParameterizedTest
    @EnumSource(LineCodes::class)
    fun `should get line location`(code: LineCodes) = testApplicationBusTracker {
        val response = getLineLocation(code.code, code.direction)
        val json = response.bodyAsText().deserialized().asArray()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json.bind().forEach {
                it["lineCode"].asString().bind().shouldBeEqualTo(code.code)
                it["codVehicle"].asString().bind()
                it["direction"].asInt().bind().shouldBeEqualTo(code.direction)
                it["service"].asString().bind()
                it["coordinates"]["latitude"].asDouble().bind()
                it["coordinates"]["longitude"].asDouble().bind()
            }
        }.getOrThrow()
    }

    @Test
    fun `should not get line location`() = testApplicationBusTracker {
        val response = getLineLocation("asdasd", 2)
        response.status.shouldBe(HttpStatusCode.NotFound)
    }

    @ParameterizedTest
    @EnumSource(LineCodes::class)
    fun `should get itineraries from line`(code: LineCodes) = testApplicationBusTracker {
        val response = getItineraries(code.code, code.direction)
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
        val response = getItineraries("asdasd", 1)
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}