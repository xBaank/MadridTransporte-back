package lines

import arrow.core.raise.either
import common.extensions.getOrThrow
import common.utils.busCodMode
import common.utils.emtCodMode
import common.utils.urbanCodMode
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
import utils.testApplicationBusTracker

enum class LocationsUrls(
    val url: String,
    val fullLineCode: String,
    val direction: Int,
    val lineCode: String,
    val codMode: Int,
) {
    EmtDirectionBased(
        "/lines/emt/6__144___/locations/2?stopCode=4597",
        "6__144___",
        2,
        "144",
        emtCodMode.toInt()
    ),
    InterurbanDirectionBased(
        "/lines/bus/8__450___/locations/1?stopCode=08242",
        "8__450___",
        1,
        "450",
        busCodMode.toInt()
    ),
    UrbanDirectionBased(
        "/lines/bus/9__2__065_/locations/2?stopCode=08242",
        "9__2__065_",
        2,
        "2",
        urbanCodMode.toInt()
    ),

    EmtCodeBased(
        "/lines/emt/itineraries/6__144____2__IT_1/locations?stopCode=4597",
        "6__144___",
        2,
        "144",
        emtCodMode.toInt()
    ),
    InterurbanCodeBased(
        "/lines/bus/itineraries/8__450____1_-_IT_1/locations?stopCode=08242",
        "8__450___",
        1,
        "450",
        busCodMode.toInt()
    ),
    UrbanCodeBased(
        "/lines/bus/itineraries/9__2__065__2_-_IT_1/locations?stopCode=08242",
        "9__2__065_",
        2,
        "2",
        urbanCodMode.toInt()
    ),
}

enum class ItinerariesUrls(val url: String, val direction: Int) {
    EmtDirectionBased("/lines/emt/6__144___/itineraries/2?stopCode=4597", 2),
    InterurbanDirectionBased("/lines/bus/8__450___/itineraries/1?stopCode=08242", 1),
    UrbanDirectionBased("/lines/bus/9__2__065_/itineraries/2?stopCode=08242", 2),

    EmtCodeBased("/lines/emt/itineraries/6__144____2__IT_1", 2),
    InterurbanCodeBased("/lines/bus/itineraries/8__450____1_-_IT_1", 1),
    UrbanCodeBased("/lines/bus/itineraries/9__2__065__2_-_IT_1", 2),
}

class BusLinesTests {
    @ParameterizedTest
    @EnumSource(LocationsUrls::class)
    fun `should get line location`(code: LocationsUrls) = testApplicationBusTracker {
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
        val response = getItineraries("asdasd", 1)
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}