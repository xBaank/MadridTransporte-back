import arrow.core.continuations.either
import busTrackerApi.extensions.getOrThrow
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import simpleJson.*
import utils.testApplicationBusTracker


class StopsTests {
    @Test
    fun `should get all stops`() = testApplicationBusTracker {
        val result = client.get("/stops/all")
        val body = result.bodyAsText().deserialized().asArray().getOrThrow()

        either {
            body.forEach {
                it["stopCode"].asString().bind().shouldNotBeEmpty()
                it["stopName"].asString().bind().shouldNotBeEmpty()
                it["stopLat"].asDouble().bind()
                it["stopLon"].asDouble().bind()
                it["codMode"].asInt().bind().shouldBeGreaterThan(0)
                it["fullStopCode"].asString().bind().shouldNotBeEmpty()
            }
        }.getOrThrow()
    }

}