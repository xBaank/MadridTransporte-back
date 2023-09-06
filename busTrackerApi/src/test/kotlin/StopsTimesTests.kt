import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.JsonObject
import simpleJson.deserialized
import utils.buildStopTimesFromJson
import utils.testApplicationBusTracker

const val busStopCode = "08242"
const val metroStopCode = "235"
const val emtStopCode = "5827"
const val tramStopCode = "4"

enum class Times(val url: String) {
    BUS("/v1/stops/bus/$busStopCode/times"),
    METRO("/v1/stops/metro/$metroStopCode/times"),
    EMT("/v1/stops/emt/$emtStopCode/times"),
    TRAM("/v1/stops/tram/$tramStopCode/times")
}

enum class TimesNotFound(val url: String) {
    BUS("/v1/stops/bus/asdasd/times"),
    METRO("/v1/stops/metro/asdasd/times"),
    EMT("/v1/stops/emt/asdasd/times"),
    TRAM("/v1/stops/tram/asdasd/times")
}

class StopsRoutingTests {
    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should get stop times`(code: Times) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonObject>()
        buildStopTimesFromJson(body).getOrElse { throw it }
    }

    @ParameterizedTest
    @EnumSource(TimesNotFound::class)
    fun `should not get stop times`(code: TimesNotFound) = testApplicationBusTracker {
        val response = client.get(code.url)
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }
}