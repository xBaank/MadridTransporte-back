import Utils.TestBase
import Utils.getFakerUserData
import Utils.register
import Utils.verify
import busTrackerApi.config.Signer
import busTrackerApi.startUp
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
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
        val signer by lazy { GlobalContext.get().get<Signer>() }

        register(mail, username, password)

        val rawToken = signer { withClaim("email", mail) }
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