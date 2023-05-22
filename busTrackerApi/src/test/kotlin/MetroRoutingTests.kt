import arrow.core.getOrElse
import busTrackerApi.plugins.configureRoutingV1
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import simpleJson.JsonObject
import simpleJson.asArray
import simpleJson.deserialized
import simpleJson.get

const val metroStopCode = "209"

class MetroRoutingTests {
    @Test
    fun should_get_metro_times() = testApplication {
        application { configureRoutingV1() }
        val response = client.get("/v1/metro/times")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["times"].asArray().getOrElse { throw it }.shouldNotBeEmpty()
    }

    @Test
    fun should_get_metros_times_by_code() = testApplication {
        application { configureRoutingV1() }
        val response = client.get("/v1/metro/times/$metroStopCode")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["times"].asArray().getOrElse { throw it }.shouldNotBeEmpty()
    }

    @Test
    fun should_not_get_metros_times_by_code() = testApplication {
        application { configureRoutingV1() }
        val response = client.get("/v1/metro/times/asdasd")

        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}