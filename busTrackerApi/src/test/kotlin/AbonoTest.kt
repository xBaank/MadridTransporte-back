import busTrackerApi.startUp
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import utils.TestBase
import utils.getAbono

class AbonoTest : TestBase {
    @Test
    fun `should get abono`() = testApplication {
        application { startUp() }
        val result = getAbono("0000000010040117584")
        result.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not get abono`() = testApplication {
        application { startUp() }
        val result = getAbono("asdasd")
        result.status.shouldBe(HttpStatusCode.NotFound)
    }
}