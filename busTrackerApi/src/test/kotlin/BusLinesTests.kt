import arrow.core.getOrElse
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import simpleJson.JsonArray
import simpleJson.asArray
import simpleJson.deserialized
import utils.*

const val interUrbanCode = "8__450___"
const val urbanCode = "9__1__074_"

class BusLinesTests : TestBase {
    @Test
    fun `should get interurban line location`() = testApplicationBusTracker {
        val response = getLineLocation(interUrbanCode)
        val json = response.bodyAsText().deserialized().asArray().getOrElse { throw it }
        response.status.shouldBe(HttpStatusCode.OK)
        json.shouldBeInstanceOf<JsonArray>()
        json.shouldNotBeEmpty()
    }

    @Test
    fun `should get urban line location`() = testApplicationBusTracker {
        val response = getLineLocation(urbanCode)
        val json = response.bodyAsText().deserialized().asArray().getOrElse { throw it }
        response.status.shouldBe(HttpStatusCode.OK)
        json.shouldBeInstanceOf<JsonArray>()
        json.shouldNotBeEmpty()
    }

    @Test
    fun `should not get urban line location`() = testApplicationBusTracker {
        val response = getLineLocation("asdasd")
        response.status.shouldBe(HttpStatusCode.NotFound)
    }

    @Test
    fun `should get interUrban stops from line`() = testApplicationBusTracker {
        val response = getStops(interUrbanCode)
        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should get urban stops from line`() = testApplicationBusTracker {
        val response = getStops(urbanCode)
        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not get stops from line`() = testApplicationBusTracker {
        val response = getStops("asdasd")
        response.status.shouldBe(HttpStatusCode.NotFound)
    }

    @Test
    fun `should get interUrban itineraries from line`() = testApplicationBusTracker {
        val response = getItineraries(interUrbanCode)
        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should get urban itineraries from line`() = testApplicationBusTracker {
        val response = getItineraries(urbanCode)
        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not get itineraries from line`() = testApplicationBusTracker {
        val response = getItineraries("asdasd")
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}