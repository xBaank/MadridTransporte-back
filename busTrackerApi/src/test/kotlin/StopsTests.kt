import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import simpleJson.JsonArray
import simpleJson.asArray
import simpleJson.deserialized
import utils.TestBase
import utils.testApplicationBusTracker

class StopsTests : TestBase {
    @Test
    fun `should get stops by query`() = testApplicationBusTracker {
        val response = client.get("/v1/stops/search?query=Leganes")
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        body.shouldNotBeEmpty()
    }

    @Test
    fun `should not get stops by query`() = testApplicationBusTracker {
        val response = client.get("/v1/stops/search?query=asdasdasdasd")
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        body.shouldBeEmpty()
    }

    @Test
    fun `should get stop by location`() = testApplicationBusTracker {
        val response = client.get("/v1/stops/locations?latitude=40.31043738780061&longitude=-3.736834949732102")
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        body.shouldNotBeEmpty()
    }

    @Test
    fun `should not get stop by location`() = testApplicationBusTracker {
        val response = client.get("/v1/stops/locations?latitude=40.asdds&longitude=-3.736834949732102")
        response.status shouldBeEqualTo HttpStatusCode.BadRequest
    }
}