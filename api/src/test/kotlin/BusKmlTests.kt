import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import utils.getKml
import utils.testApplicationBusTracker

class BusKmlTests {
    @ParameterizedTest
    @EnumSource(ItinerariesCodes::class)
    fun `should get bus kml`(code: ItinerariesCodes) = testApplicationBusTracker {
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