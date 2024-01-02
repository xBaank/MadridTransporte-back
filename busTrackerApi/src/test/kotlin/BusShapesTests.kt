import arrow.core.continuations.either
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.*
import utils.getShapes
import utils.testApplicationBusTracker

enum class ShapeCodes(val code: String) {
    Emt("6__144____1__IT_1"),
    Interurban("8__450____1_-_IT_1"),
    Urban("9__2__065__2_-_IT_1"),
}

class BusShapesTests {
    @ParameterizedTest
    @EnumSource(ShapeCodes::class)
    fun `should get bus shapes`(code: ShapeCodes) = testApplicationBusTracker {
        val response = getShapes(code.code)
        val json = response.bodyAsText().deserialized().asArray()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json.bind().shouldNotBeEmpty()
            json.bind().forEach {
                it["sequence"].asInt().bind()
                it["longitude"].asDouble().bind()
                it["latitude"].asDouble().bind()
            }
        }
    }

    @Test
    fun `should not get bus shapes`() = testApplicationBusTracker {
        val response = getShapes("asdasd")
        val json = response.bodyAsText().deserialized().asArray()

        response.status.shouldBe(HttpStatusCode.OK)
        either {
            json.bind().shouldBeEmpty()
        }
    }
}