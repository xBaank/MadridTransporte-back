import arrow.core.getOrElse
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.JsonArray
import simpleJson.JsonObject
import simpleJson.deserialized
import simpleJson.get
import utils.testApplicationBusTracker

const val busStopCode = "08242"
const val trainStopCode = "41"
const val metroStopCode = "235"
const val emtStopCode = "2445"

enum class Times(val url: String) {
    BUS("/v1/stops/bus/$busStopCode/times"),
    TRAIN("/v1/stops/train/$trainStopCode/times"),
    METRO("/v1/stops/metro/$metroStopCode/times"),
    EMT("/v1/stops/emt/$emtStopCode/times")
}

enum class TimesNotFound(val url: String) {
    BUS("/v1/stops/bus/asdasd/times"),
    TRAIN("/v1/stops/train/asdasd/times"),
    METRO("/v1/stops/metro/asdasd/times"),
    EMT("/v1/stops/emt/asdasd/times")
}

class StopsRoutingTests {

    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should get stop times`(code: Times) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
    }

    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should get stop times cached`(code: Times) = testApplicationBusTracker {
        val response = client.get(code.url)
        val responseCached = client.get(code.url + "/cached")

        responseCached.status.isSuccess().shouldBe(true)
        response.status.isSuccess().shouldBe(true)

        val body = response.bodyAsText().deserialized()
        val bodyCached = responseCached.bodyAsText().deserialized()

        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        bodyCached.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        bodyCached["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["lastTime"].getOrElse { throw it } shouldBeEqualTo bodyCached["lastTime"].getOrElse { throw it }
    }


    @ParameterizedTest
    @EnumSource(TimesNotFound::class)
    fun `should not get stop times`(code: TimesNotFound) = testApplicationBusTracker {
        val response = client.get(code.url)
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }

    @ParameterizedTest
    @EnumSource(Times::class)
    fun `should subscribe to stopTimes`(code: Times) = testApplicationBusTracker { client ->
        client.webSocket({
            url(code.url + "/subscribe")
        })
        {
            val response = incoming.receive() as? Frame.Text
            val body = response?.readText()?.deserialized()

            response.shouldBeInstanceOf<Frame.Text>()
            body!!.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
            body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
            body["lastTime"].getOrElse { throw it }.shouldNotBeNull()
            close()
        }
    }

    @ParameterizedTest
    @EnumSource(TimesNotFound::class)
    fun `should not find stop when subscribed`(code: TimesNotFound) = testApplicationBusTracker { client ->
        runCatching {
            client.webSocket({
                url(code.url + "/subscribe")
            })
            {
                val response = incoming.receive() as? Frame.Text
                val body = response?.readText()?.deserialized()

                response.shouldBeInstanceOf<Frame.Text>()
                body!!.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
                body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
                body["lastTime"].getOrElse { throw it }.shouldNotBeNull()
                close()
            }
        }.exceptionOrNull()!!.shouldBeInstanceOf<ClosedReceiveChannelException>()
    }
}