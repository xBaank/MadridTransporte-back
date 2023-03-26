import crtm.CRTMClient
import crtm.extensions.getRoute
import crtm.utils.createLineCode
import crtm.utils.createStopCode
import kotlinx.coroutines.runBlocking
import lines.CodLine
import modes.CodMode
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import stops.CodStop

class UtilsTest {
    @Test
    fun `should create codStop`() {
        createStopCode(CodMode(4), "07904") shouldBeEqualTo CodStop("4_07904")
    }

    @Test
    fun `should create lineCode`() {
        createLineCode(CodMode(4), "450") shouldBeEqualTo CodLine("4__450___")
    }

    @Test
    @Disabled
    fun `should get route`(): Unit = runBlocking {
        val client = CRTMClient()
        val line = client.lines.getLineInfoByCodLine(CodLine("8__450___"))
        val route = line.itineraries.first().stops.map { it.coordinates }
            .getRoute(apiKey = "")
        route.shouldNotBeEmpty()
    }
}