import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import utils.TestBase
import utils.getAbono
import utils.testApplicationBusTracker

class AbonoTest : TestBase {
    @Test
    fun `should get abono`() = testApplicationBusTracker {
        val result = getAbono("0000000010040117584")
        result.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not get abono`() = testApplicationBusTracker {
        val result = getAbono("asdasd")
        result.status.shouldBe(HttpStatusCode.NotFound)
    }
}