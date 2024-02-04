import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import utils.getKml
import utils.testApplicationBusTracker

enum class KmlCodes(val code: String) {
    Emt("6__144____1__IT_1"),
    Interurban("8__450____1_-_IT_1"),
    Urban("9__2__065__2_-_IT_1"),
}

class BusKmlTests {
    @ParameterizedTest
    @EnumSource(ShapeCodes::class)
    fun `should get bus kml`(code: ShapeCodes) = testApplicationBusTracker {
        val response = getKml(code.code)
        val xml = response.bodyAsText()

        response.status.shouldBe(HttpStatusCode.OK)
        xml.shouldNotBeEmpty()
    }

    @Test
    fun `should not get bus kml`() = testApplicationBusTracker {
        val response = getKml("asdasd")
        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}