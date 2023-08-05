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
import utils.testApplicationBusTracker

class StopsTests {
    @Test
    fun `should get stops`() = testApplicationBusTracker {
        val response = client.get("/v1/stops/all")
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        body.shouldNotBeEmpty()
    }
}