import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.JsonArray
import simpleJson.deserialized
import utils.testApplicationBusTracker


enum class Alerts(val url: String) {
    BUS("/v1/stops/bus/alerts"),
    TRAIN("/v1/stops/train/alerts"),
    METRO("/v1/stops/metro/alerts"),
    TRAM("/v1/stops/tram/alerts"),
    EMT("/v1/stops/emt/alerts")
}

class AlertsTest {
    @ParameterizedTest
    @EnumSource(Alerts::class)
    fun `should get alerts`(code: Alerts) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
    }
}