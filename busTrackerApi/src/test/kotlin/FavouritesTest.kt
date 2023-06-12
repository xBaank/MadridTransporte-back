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
import org.litote.kmongo.reactivestreams.KMongo
import simpleJson.JsonArray
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get
import java.net.URLEncoder

class FavouritesTest {

    @BeforeEach
    fun setUp() {
        KMongo.createClient(mongoDBContainer.connectionString).getDatabase("test").drop()
        System.setProperty("MONGO_CONNECTION_STRING", mongoDBContainer.connectionString)
        System.setProperty("MONGO_DATABASE_NAME", "test")
        //drop
    }

    @AfterEach()
    fun tearDown() {
        GlobalContext.stopKoin()
    }

    @Test
    fun `should get favourites`() = testApplication {
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
        val accessToken = login(mail, password).bodyAsText().deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = getFavourites(accessToken)
        favouritesResponse.status.shouldBe(HttpStatusCode.OK)
        favouritesResponse.bodyAsText().deserialized().getOrElse { throw it }.shouldBeInstanceOf(JsonArray::class)
    }

    @Test
    fun `should add favourites`() = testApplication {
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
        val response = login(mail, password).bodyAsText()
        val accessToken = response.deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = addFavourite(accessToken, "bus", "123")
        favouritesResponse.status.shouldBe(HttpStatusCode.Created)
    }

    @Test
    fun `should add and delete`() = testApplication {
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
        val response = login(mail, password).bodyAsText()
        val accessToken = response.deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = addFavourite(accessToken, "bus", "123")
        val deletedResponse = deleteFavourite(accessToken, "123")

        favouritesResponse.status.shouldBe(HttpStatusCode.Created)
        deletedResponse.status.shouldBe(HttpStatusCode.NoContent)
    }

    @Test
    fun `should add and getById`() = testApplication {
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
        val response = login(mail, password).bodyAsText()
        val accessToken = response.deserialized()["token"].asString().getOrElse { throw it }

        val favouritesResponse = addFavourite(accessToken, "bus", "123")
        val byIdResponse = getFavourite(accessToken, "123")

        favouritesResponse.status.shouldBe(HttpStatusCode.Created)
        byIdResponse.status.shouldBe(HttpStatusCode.OK)
    }

    @Test
    fun `should not add favourites`() = testApplication {
        application { startUp() }

        val favouritesResponse = addFavourite("asd", "bus", "123")
        favouritesResponse.status.shouldBe(HttpStatusCode.Unauthorized)
    }
}