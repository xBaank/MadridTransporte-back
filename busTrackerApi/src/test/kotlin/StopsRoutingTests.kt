import arrow.core.getOrElse
import busTrackerApi.plugins.configureRoutingV1
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import simpleJson.JsonArray
import simpleJson.JsonObject
import simpleJson.deserialized
import simpleJson.get

const val busStopCode = "08242"

class StopsRoutingTests {
    @Test
    fun should_get_stop_times() = testApplication {
        application { configureRoutingV1() }
        val response = client.get("/v1/bus/stops/$busStopCode/times")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
    }


    @Test
    fun should_get_stop_times_cached() = testApplication {
        application { configureRoutingV1() }
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
    fun should_not_get_stop_times() = testApplication {
        application { configureRoutingV1() }
        val response = client.get("/v1/bus/stops/aasdsad/times")
        response.status shouldBeEqualTo HttpStatusCode.BadRequest
    }
}