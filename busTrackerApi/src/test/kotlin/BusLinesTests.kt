import arrow.core.getOrElse
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.JsonArray
import simpleJson.asArray
import simpleJson.deserialized
import utils.getItineraries
import utils.getLineLocation
import utils.testApplicationBusTracker

enum class LineCodes(val code: String, val direction: Int) {
    Interurban("8__450___", 1),
    Urban("9__1__074_", 1)
}

class BusLinesTests {
    @ParameterizedTest
    @EnumSource(LineCodes::class)
    fun `should get interurban line location`(code: LineCodes) = testApplicationBusTracker {
        val response = getLineLocation(code.code, code.direction)
        val json = response.bodyAsText().deserialized().asArray().getOrElse { throw it }
        response.status.shouldBe(HttpStatusCode.OK)
        json.shouldBeInstanceOf<JsonArray>()
    }

    @Test
    fun `should not get urban line location`() = testApplicationBusTracker {
        val response = getLineLocation("asdasd", 2)
        response.status.shouldBe(HttpStatusCode.NotFound)
    }

    @ParameterizedTest
    @EnumSource(LineCodes::class)
    fun `should get interUrban itineraries from line`(code: LineCodes) = testApplicationBusTracker {
        val response = getItineraries(code.code, code.direction)
        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not get itineraries from line`() = testApplicationBusTracker {
        val response = getItineraries("asdasd", 1)
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}