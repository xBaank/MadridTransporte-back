import arrow.core.continuations.either
import busTrackerApi.extensions.getOrThrow
import busTrackerApi.routing.stops.bus.busCodMode
import busTrackerApi.routing.stops.emt.emtCodMode
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
import utils.getItineraries
import utils.getLineLocation
import utils.testApplicationBusTracker

enum class LocationsCodes(
    val url: String,
    val fullLineCode: String,
    val direction: Int,
    val lineCode: String,
    val codMode: Int
) {
    Emt("/lines/emt/6__144___/locations/2", "6__144___", 2, "144", emtCodMode.toInt()),
    Interurban("/lines/bus/8__450___/locations/1", "8__450___", 1, "450", busCodMode.toInt()),
    Urban("/lines/bus/9__1__074_/locations/1", "9__1__074_", 1, "1", 9),
    WeirdUrban("/lines/bus/9__1__058_/locations/1", "9__1__058_", 1, "1", 9)
}

enum class ItinerariesCodes(val url: String, val code: String, val simpleLineCode: String, val direction: Int) {
    Emt("/lines/emt/6__144___/itineraries/2", "6__144___", "144", 2),
    Interurban("/lines/bus/8__450___/itineraries/1", "8__450___", "450", 1),
    Urban("/lines/bus/9__1__074_/itineraries/1", "9__1__074_", "1", 1),
    WeirdUrban("/lines/bus/9__1__058_/itineraries/1", "9__1__058_", "1", 1)
}

class BusLinesTests {
    @ParameterizedTest
    @EnumSource(LocationsCodes::class)
    fun `should get line location`(code: LocationsCodes) = testApplicationBusTracker {
        val response = client.get(code.url)
        val json = response.bodyAsText().deserialized().asObject()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json["codMode"].asInt().bind().shouldBeEqualTo(code.codMode)
            json["lineCode"].asString().bind().shouldBeEqualTo(code.lineCode)
            json["locations"].asArray().bind().forEach {
                it["lineCode"].asString().bind().shouldBeEqualTo(code.fullLineCode)
                it["simpleLineCode"].asString().bind().shouldBeEqualTo(code.lineCode)
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
    @EnumSource(ItinerariesCodes::class)
    fun `should get itineraries from line`(code: ItinerariesCodes) = testApplicationBusTracker {
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
        val response = getItineraries("asdasd", 1)
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}