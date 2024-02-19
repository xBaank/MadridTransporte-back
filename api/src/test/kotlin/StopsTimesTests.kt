import api.extensions.getOrThrow
import arrow.core.continuations.either
import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.*
import utils.testApplicationBusTracker

const val busStopCode = "08242"
const val trainStopCode = "34"
const val metroStopCode = "235"
const val emtStopCode = "5827"
const val tramStopCode = "58"

enum class Times(val url: String) {
    BUS("/stops/bus/$busStopCode/times"),
    TRAIN("/stops/train/$trainStopCode/times"),
    METRO("/stops/metro/$metroStopCode/times"),
    EMT("/stops/emt/$emtStopCode/times"),
    TRAM("/stops/tram/$tramStopCode/times")
}

enum class TimesNotFound(val url: String) {
    BUS("/stops/bus/asdasd/times"),
    TRAIN("/stops/train/asdasd/times"),
    METRO("/stops/metro/asdasd/times"),
    EMT("/stops/emt/asdasd/times"),
    TRAM("/stops/tram/asdasd/times")
}

class StopsTimesTests {
    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should get stop times`(code: Times) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized().getOrElse { throw it }

        response.status.value.shouldBeEqualTo(200)
        body.shouldBeInstanceOf<JsonObject>()
        either {
            body["codMode"].asInt().bind()
            body["stopName"].asString().bind()
            body["simpleStopCode"].asString().bind()
            body["stopCode"].asString().bind()
            body["coordinates"]["latitude"].asDouble().bind()
            body["coordinates"]["longitude"].asDouble().bind()
            body["arrives"].asArray().bind().forEach {
                it["codMode"].asInt().bind()
                it["line"].asString().bind()
                it["anden"].asInt().getOrNull()
                it["destination"].asString().bind()
                it["estimatedArrives"].asArray().bind().map { it.asLong().bind() }.first()
            }
            body["incidents"].asArray().bind().forEach {
                it["title"].asString().bind()
                it["description"].asString().bind()
                it["from"].asString().bind()
                it["to"].asString().bind()
                it["cause"].asString().bind()
                it["effect"].asString().bind()
                it["url"].asString().bind()
            }
        }.getOrThrow()
    }

    @ParameterizedTest
    @EnumSource(TimesNotFound::class)
    fun `should not get stop times`(code: TimesNotFound) = testApplicationBusTracker {
        val response = client.get(code.url)
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }
}