import MongoContainer.mongoDBContainer
import arrow.core.getOrElse
import busTrackerApi.startUp
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import org.litote.kmongo.reactivestreams.KMongo
import simpleJson.JsonArray
import simpleJson.asArray
import simpleJson.deserialized

const val metroStopCode = "209"

class MetroRoutingTests {

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
    fun should_get_metro_times() = testApplication {
        application { startUp() }
        val response = client.get("/v1/metro/times")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
        body.asArray().getOrElse { throw it }.shouldNotBeEmpty()
    }

    @Test
    fun should_get_metros_times_by_code() = testApplication {
        application { startUp() }
        val response = client.get("/v1/metro/times/$metroStopCode")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
        body.asArray().getOrElse { throw it }.shouldNotBeEmpty()
    }

    @Test
    fun should_not_get_metros_times_by_code() = testApplication {
        application { startUp() }
        val response = client.get("/v1/metro/times/asdasd")

        response.status.shouldBe(HttpStatusCode.NotFound)
    }
}