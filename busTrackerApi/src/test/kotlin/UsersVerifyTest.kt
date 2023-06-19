import busTrackerApi.startUp
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import utils.*
import java.net.URLEncoder

class UsersVerifyTest : TestBase {

    @Test
    fun `should register then verify`() = testApplication {
        application {
            startUp()
            routing {
                get("/ping") {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()

        register(mail, username, password)

        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")

        val response = verify(token)

        response.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not verify`() = testApplication {
        application { startUp() }
        val token = "ASdasd"

        val response = verify(token)

        response.status.shouldBe(HttpStatusCode.Unauthorized)
    }
}