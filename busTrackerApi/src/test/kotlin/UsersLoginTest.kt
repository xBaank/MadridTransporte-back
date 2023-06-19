import arrow.core.getOrElse
import busTrackerApi.startUp
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import simpleJson.JsonObject
import simpleJson.deserialized
import utils.*
import java.net.URLEncoder


class UsersLoginTest : TestBase {

    @Test
    fun `should register then verify then login`() = testApplication {
        application { startUp() }
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()

        register(mail, username, password)
        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val response = login(mail, password)


        response.status.shouldBe(HttpStatusCode.OK)
        response.bodyAsText().deserialized().getOrElse { throw it }.shouldBeInstanceOf(JsonObject::class)
    }

    @Test
    fun `should not login with not found username`() = testApplication {
        application { startUp() }
        val (mail, _, password) = getFakerUserData()

        val response = login(mail, password)

        response.status.shouldBe(HttpStatusCode.NotFound)
    }

    @Test
    fun `should not login with incorrect credentials`() = testApplication {
        application { startUp() }
        val (mail, username, password) = getFakerUserData()
        val (_, registerSigner, _) = getSigners()

        register(mail, username, password)
        val rawToken = registerSigner.value { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val response = login(mail, "asd")

        response.status.shouldBe(HttpStatusCode.Unauthorized)
    }
}