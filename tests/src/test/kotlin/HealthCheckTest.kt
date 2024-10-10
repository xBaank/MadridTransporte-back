import arrow.core.raise.either
import common.extensions.getOrThrow
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import simpleJson.asBoolean
import simpleJson.deserialized
import simpleJson.get
import utils.testApplicationBusTracker

class HealthCheckTest {
    @Test
    fun `should check health`() = testApplicationBusTracker {
        val response = client.get("/health")
        val json = response.bodyAsText().deserialized()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json["isRunning"].asBoolean().bind().shouldBe(true)
        }.getOrThrow()
    }
}