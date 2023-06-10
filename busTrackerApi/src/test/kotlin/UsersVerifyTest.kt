import MongoContainer.mongoDBContainer
import busTrackerApi.plugins.Signer
import busTrackerApi.startUp
import io.github.serpro69.kfaker.faker
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.stopKoin
import org.litote.kmongo.reactivestreams.KMongo
import java.net.URLEncoder

class UsersVerifyTest {
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
    fun `should register then verify`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val mail = faker.internet.safeEmail()
        val username = faker.name.name()
        val password = faker.worldOfWarcraft.hero()
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