import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import simpleJson.deserialized

class StopsRoutingTests {
    @Test
    fun should_get_stopTimes() = testApplication {
        val result = client.get("/v1/stops/08242/times")
        val jsonResult = result.bodyAsText().deserialized().getOrElse { throw it }
    }
}