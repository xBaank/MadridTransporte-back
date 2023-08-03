import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.JsonNode
import simpleJson.JsonObject
import simpleJson.deserialized
import simpleJson.get
import utils.testApplicationBusTracker

const val busStopCode = "08242"
const val trainStopCode = "41"
const val destinationStopCode = "53"
const val metroStopCode = "235"
const val emtStopCode = "2445"
const val tramStopCode = "4"

enum class Times(val url: String, val urlCached: String) {
    BUS("/v1/stops/bus/$busStopCode/times", "/v1/stops/bus/$busStopCode/times/cached"),
    TRAIN(
        "/v1/stops/train/times?originStopCode=$trainStopCode&destinationStopCode=$destinationStopCode",
    "/v1/stops/train/times/cached?originStopCode=$trainStopCode&destinationStopCode=$destinationStopCode"
    ),
    METRO("/v1/stops/metro/$metroStopCode/times", "/v1/stops/metro/$metroStopCode/times/cached"),
    EMT("/v1/stops/emt/$emtStopCode/times", "/v1/stops/emt/$emtStopCode/times/cached"),
    TRAM("/v1/stops/tram/$tramStopCode/times", "/v1/stops/tram/$tramStopCode/times/cached")
}

enum class TimesNotFound(val url: String) {
    BUS("/v1/stops/bus/asdasd/times"),
    TRAIN("/v1/stops/train/times?originStopCode=asdasd&destinationStopCode=asdasd"),
    METRO("/v1/stops/metro/asdasd/times"),
    EMT("/v1/stops/emt/asdasd/times"),
    TRAM("/v1/stops/tram/asdasd/times")
}

class StopsRoutingTests {

    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should get stop times`(code: Times) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonNode>()
    }

    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should get stop times cached`(code: Times) = testApplicationBusTracker {
        val response = client.get(code.url)
        val responseCached = client.get(code.urlCached)

        responseCached.status.isSuccess().shouldBe(true)
        response.status.isSuccess().shouldBe(true)

        val body = response.bodyAsText().deserialized()
        val bodyCached = responseCached.bodyAsText().deserialized()

        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        bodyCached.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonNode>()
        bodyCached["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonNode>()
        body["lastTime"].getOrElse { throw it } shouldBeEqualTo bodyCached["lastTime"].getOrElse { throw it }
    }


    @ParameterizedTest
    @EnumSource(TimesNotFound::class)
    fun `should not get stop times`(code: TimesNotFound) = testApplicationBusTracker {
        val response = client.get(code.url)
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }
}