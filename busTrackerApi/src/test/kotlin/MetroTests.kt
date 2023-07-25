import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import simpleJson.JsonArray
import simpleJson.asArray
import simpleJson.deserialized
import utils.TestBase
import utils.testApplicationBusTracker

const val metroStationCode = "209"

class MetroRoutingTests : TestBase {


    @Test
    fun `should get metro times`() = testApplicationBusTracker {
        val response = client.get("/v1/metro/times")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
        body.asArray().getOrElse { throw it }.shouldNotBeEmpty()
    }

    @Test
    fun `should get metros times by code`() = testApplicationBusTracker {
        val response = client.get("/v1/metro/times/$metroStationCode")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
        body.asArray().getOrElse { throw it }.shouldNotBeEmpty()
    }

    @Test
    fun `should not get metros times by code`() = testApplicationBusTracker {
        val response = client.get("/v1/metro/times/asdasd")

        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}