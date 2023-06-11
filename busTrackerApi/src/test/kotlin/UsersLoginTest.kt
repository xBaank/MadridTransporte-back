import MongoContainer.mongoDBContainer
import arrow.core.getOrElse
import busTrackerApi.config.Signer
import busTrackerApi.startUp
import io.github.serpro69.kfaker.faker
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.stopKoin
import org.litote.kmongo.reactivestreams.KMongo
import simpleJson.JsonObject
import simpleJson.deserialized
import java.net.URLEncoder


class UsersLoginTest {

    @BeforeEach
    fun setUp() {
        KMongo.createClient(mongoDBContainer.connectionString).getDatabase("test").drop()
        System.setProperty("MONGO_CONNECTION_STRING", mongoDBContainer.connectionString)
        System.setProperty("MONGO_DATABASE_NAME", "test")
        //drop
    }

    @AfterEach()
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should register then verify then login`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val signer by lazy { GlobalContext.get().get<Signer>() }
        val username = faker.name.name()
        val mail = faker.internet.safeEmail()
        val password = faker.crypto.md5()

        register(mail, username, password)
        val rawToken = signer { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val response = login(mail, password)


        response.status.shouldBe(HttpStatusCode.OK)
        response.bodyAsText().deserialized().getOrElse { throw it }.shouldBeInstanceOf(JsonObject::class)
    }

    @Test
    fun `should not login with not found username`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val mail = faker.internet.safeEmail()
        val password = faker.crypto.md5()

        val response = login(mail, password)

        response.status.shouldBe(HttpStatusCode.NotFound)
    }

    @Test
    fun `should not login with incorrect credentials`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val signer by lazy { GlobalContext.get().get<Signer>() }
        val username = faker.name.name()
        val mail = faker.internet.safeEmail()
        val password = faker.crypto.md5()


        register(mail, username, password)
        val rawToken = signer { withClaim("email", mail) }
        val token = URLEncoder.encode(rawToken, "UTF-8")
        verify(token)
        val response = login(mail, "asd")

        response.status.shouldBe(HttpStatusCode.Unauthorized)
    }
}