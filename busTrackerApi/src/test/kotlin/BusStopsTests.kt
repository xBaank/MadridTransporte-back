import arrow.core.getOrElse
import busTrackerApi.config.AuthSignerQualifier
import busTrackerApi.config.Signer
import busTrackerApi.config.dbModule
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import simpleJson.*
import utils.TestBase
import utils.testApplicationBusTracker

const val busStopCode = "08242"

class StopsRoutingTests : TestBase, KoinComponent {

    @Test
    fun `should get stop times`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/$busStopCode/times")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
    }

    @Test
    fun `should get stop estimations`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/$busStopCode/estimations")
        val body = response.bodyAsText().deserialized()

        response.status.isSuccess().shouldBe(true)
        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
    }

    @Test
    fun `should get stops by query`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/query?search=Avda. de la Peseta")
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        body.shouldNotBeEmpty()
    }

    @Test
    fun `should not get stop estimations`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/asdasd/estimations")
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }

    @Test
    fun `should get stop by location`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/locations?latitude=40.31043738780061&longitude=-3.736834949732102")
        val body = response.bodyAsText().deserialized().asArray().getOrElse { throw it }

        response.status.isSuccess().shouldBe(true)
        body.shouldBeInstanceOf<JsonArray>()
        body.shouldNotBeEmpty()
    }

    @Test
    fun `should not get stop by location`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/locations?latitude=40.asdds&longitude=-3.736834949732102")
        response.status shouldBeEqualTo HttpStatusCode.BadRequest
    }


    @Test
    fun `should get stop times cached`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/$busStopCode/times")
        val responseCached = client.get("/v1/bus/stops/$busStopCode/times/cached")

        responseCached.status.isSuccess().shouldBe(true)
        response.status.isSuccess().shouldBe(true)

        val body = response.bodyAsText().deserialized()
        val bodyCached = responseCached.bodyAsText().deserialized()

        body.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        bodyCached.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        bodyCached["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
        body["lastTime"].getOrElse { throw it } shouldBeEqualTo bodyCached["lastTime"].getOrElse { throw it }
    }


    @Test
    fun `should not get stop times`() = testApplicationBusTracker {
        val response = client.get("/v1/bus/stops/aasdsad/times")
        response.status shouldBeEqualTo HttpStatusCode.NotFound
    }

    @Test
    fun `should subscribe to stopTimes`() = testApplicationBusTracker { client ->
        //Fix as koin is not started, and we need config before using the client
        val koinApp = startKoin { modules(dbModule) }
        val authSigner by koinApp.koin.inject<Signer>(AuthSignerQualifier)
        val token = authSigner { withClaim("email", "whatever") }
        stopKoin()

        client.webSocket({
            url("/v1/bus/stops/$busStopCode/times/subscribe")
            header("Authorization", "Bearer $token")
        })
        {
            val response = incoming.receive() as? Frame.Text
            val body = response?.readText()?.deserialized()

            response.shouldBeInstanceOf<Frame.Text>()
            body!!.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
            body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
            body["lastTime"].getOrElse { throw it }.shouldNotBeNull()
            close()
        }
    }

    @Test
    fun `should get unauthorized when subscribe to stopTimes`() = testApplicationBusTracker { client ->
        runCatching {
            client.webSocket({
                url("/v1/bus/stops/$busStopCode/times/subscribe")
            })
            {
                val response = incoming.receive() as? Frame.Text
                val body = response?.readText()?.deserialized()

                response.shouldBeInstanceOf<Frame.Text>()
                body!!.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
                body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
                body["lastTime"].getOrElse { throw it }.shouldNotBeNull()
                close()
            }
        }.exceptionOrNull()!!.shouldBeInstanceOf<IllegalStateException>()
    }

    @Test
    fun `should not find stop when subscribed`() = testApplicationBusTracker { client ->
        //Fix as koin is not started, and we need config before using the client
        val koinApp = startKoin { modules(dbModule) }
        val authSigner by koinApp.koin.inject<Signer>(AuthSignerQualifier)
        val token = authSigner { withClaim("email", "whatever") }
        stopKoin()

        runCatching {
            client.webSocket({
                url("/v1/bus/stops/asdasd/times/subscribe")
                header("Authorization", "Bearer $token")
            })
            {
                val response = incoming.receive() as? Frame.Text
                val body = response?.readText()?.deserialized()

                response.shouldBeInstanceOf<Frame.Text>()
                body!!.getOrElse { throw it }.shouldBeInstanceOf<JsonObject>()
                body["data"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
                body["lastTime"].getOrElse { throw it }.shouldNotBeNull()
                close()
            }
        }.exceptionOrNull()!!.shouldBeInstanceOf<ClosedReceiveChannelException>()
    }
}