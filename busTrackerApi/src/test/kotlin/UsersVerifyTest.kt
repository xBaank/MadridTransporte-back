import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import utils.*
import java.net.URLEncoder

class UsersVerifyTest : TestBase {

    @Test
    fun `should register then verify`() = testApplicationBusTracker(pingStartUp) {
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()

        register(mail, username, password)

        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")

        val response = verify(token)

        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not verify`() = testApplicationBusTracker {
        val token = "ASdasd"

        val response = verify(token)

        response.status.shouldBe(HttpStatusCode.Unauthorized)
    }
}