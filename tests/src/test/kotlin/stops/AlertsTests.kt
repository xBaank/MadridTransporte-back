package stops

import arrow.core.getOrElse
import arrow.core.raise.either
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.*
import utils.testApplicationBusTracker


enum class Alerts(val url: String) {
    BUS("/stops/bus/alerts"),
    TRAIN("/stops/train/alerts"),
    METRO("/stops/metro/alerts"),
    TRAM("/stops/tram/alerts"),
    EMT("/stops/emt/alerts")
}

class AlertsTest {
    @ParameterizedTest
    @EnumSource(Alerts::class)
    fun `should get alerts`(code: Alerts) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        either {
            body.asArray().bind().forEach {
                it["codMode"].bind().shouldBeInstanceOf<JsonNumber>()
                it["codLine"].bind().shouldBeInstanceOf<JsonString>()
                it["description"].bind().shouldBeInstanceOf<JsonString>()
                it["stops"].bind().shouldBeInstanceOf<JsonArray>()
            }
        }.getOrElse { throw it }
    }
}