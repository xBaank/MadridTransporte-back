import arrow.core.getOrElse
import busTrackerApi.config.Signer
import busTrackerApi.startUp
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get
import utils.*
import java.net.URLEncoder

class UserResetPassword : TestBase {

    @Test
    fun `should reset password then login`() = testApplication {
        application {
            startUp()
            routing {
                get("/ping") {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        val signer by lazy { GlobalContext.get().get<Signer>() }
        val (mail, username, password) = getFakerUserData()
        val newPassword = "newPassword"


        register(mail, username, password)
        val rawToken = signer { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val accessToken = login(mail, password).bodyAsText().deserialized()["token"].asString().getOrElse { throw it }
        val response = sendResetPassword(mail)
        val response2 = resetPassword(accessToken, newPassword)
        val response3 = login(mail, newPassword)

        response.status.shouldBe(HttpStatusCode.OK)
        response2.status.shouldBe(HttpStatusCode.OK)
        response3.status.shouldBe(HttpStatusCode.OK)
        response3.bodyAsText().deserialized()["token"].asString().getOrElse { throw it }
    }
}