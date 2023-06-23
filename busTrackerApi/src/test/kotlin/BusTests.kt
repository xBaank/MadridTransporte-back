import arrow.core.getOrElse
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import simpleJson.JsonArray
import simpleJson.JsonObject
import simpleJson.deserialized
import simpleJson.get
import utils.TestBase
import utils.testApplicationBusTracker

const val busStopCode = "08242"

class StopsRoutingTests : TestBase {

    @Test
    fun `should get stop times`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/$busStopCode/times")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
    }


    @Test
    fun `should get stop times cached`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/$busStopCode/times")
        val responseCached = client.get("/v1/bus/stops/$busStopCode/times/cached")

        responseCached.status.isSuccess().shouldBe(true)
        response.status.isSuccess().shouldBe(true)

        val body = response.bodyAsText().deserialized()
        val bodyCached = responseCached.bodyAsText().deserialized()

        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        bodyCached.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
        bodyCached["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
        body["lastTime"].getOrElse { throw it } shouldBeEqualTo bodyCached["lastTime"].getOrElse { throw it }
    }


    @Test
    fun `should not get stop times`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/aasdsad/times")
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }

    @Test
    fun `should subscribe to stopTimes`() = testApplicationBusTracker { client ->
        client.webSocket({
            url("/v1/bus/stops/$busStopCode/times/subscribe")
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
    }
}