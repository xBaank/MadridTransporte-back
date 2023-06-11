import MongoContainer.mongoDBContainer
import busTrackerApi.errorObject
import busTrackerApi.startUp
import io.github.serpro69.kfaker.faker
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.stopKoin
import org.litote.kmongo.reactivestreams.KMongo
import simpleJson.jObject
import simpleJson.serialized

class UsersRegisterTest {

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
    fun `should register`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val mail = faker.internet.safeEmail()
        val username = faker.name.name()
        val password = faker.worldOfWarcraft.hero()

        val response = register(mail, username, password)

        response.status.shouldBe(HttpStatusCode.Created)
    }

    @Test
    fun `should not register`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val username = faker.name.name()
        val password = faker.worldOfWarcraft.hero()

        val response = register("", username, password)

        response.status.shouldBe(HttpStatusCode.BadRequest)
        response.bodyAsText().shouldBeEqualTo(errorObject("Invalid mail"))
    }

    @Test
    fun `should not register with missing redirect`() = testApplication {
        application { startUp() }
        val faker = faker { }
        val mail = faker.internet.safeEmail()
        val username = faker.name.name()
        val password = faker.worldOfWarcraft.hero()

        val response = client.post("/v1/users/register") {
            contentType(ContentType.Application.Json)
            setBody(jObject {
                "email" += mail
                "username" += username
                "password" += password
            }.serialized())
        }

        response.status.shouldBe(HttpStatusCode.BadRequest)
        response.bodyAsText().shouldBeEqualTo(errorObject("Missing redirectUrl"))
    }

    @Test
    fun `should not register already existing`() = testApplication {
        application { startUp() }
        val faker = faker {}
        val mail = faker.internet.safeEmail()
        val username = faker.name.name()
        val password = faker.worldOfWarcraft.hero()

        register(mail, username, password)


        val response = register(mail, username, password)
        response.status.shouldBe(HttpStatusCode.Conflict)
    }
}