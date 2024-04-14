import api.extensions.getOrThrow
import arrow.core.getOrElse
import arrow.core.raise.either
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.*
import utils.testApplicationBusTracker

enum class TimesPlanned(val url: String) {
    BUS("/stops/bus/$busStopCode/planned"),
}

enum class TimesPlannedNotFound(val url: String) {
    BUS("/stops/bus/asdasd/planned"),
}

class StopsTimesPlannedTests {
    @ParameterizedTest
    @EnumSource(TimesPlanned::class)
    fun `should get stop times`(code: TimesPlanned) = testApplicationBusTracker {
        val response = client.get(code.url)
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.value.shouldBeEqualTo(200)
        either {
            body.forEach {
                it["lineCode"].asString().bind()
                it["direction"].asInt().bind()
                it["itineraryCode"].asString().bind()
                it["arrives"].asArray().getOrThrow().forEach { arrive ->
                    arrive.asLong().bind()
                }
            }
        }.getOrThrow()
    }

    @ParameterizedTest
    @EnumSource(TimesPlannedNotFound::class)
    fun `should not get stop times`(code: TimesPlannedNotFound) = testApplicationBusTracker {
        val response = client.get(code.url)
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }
}