import arrow.core.continuations.either
import arrow.core.getOrElse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import simpleJson.*
import utils.testApplicationBusTracker

const val trainStopCode = "41"
const val destinationStopCode = "53"

class StopsTrainsTimesTests {
    @Test
    fun `should get stop times`() = testApplicationBusTracker {
        val url = "/stops/train/times?originStopCode=$trainStopCode&destinationStopCode=$destinationStopCode"
        val response = client.get(url)
        val body = response.bodyAsText().deserialized().getOrElse { throw it }
        body.shouldBeInstanceOf<JsonObject>()
        either {
            body["actTiempoReal"].bind().shouldBeInstanceOf<JsonBoolean>()
            body["peticion"].bind().shouldBeInstanceOf<JsonObject>()
            body["peticion"]["cdgoEstOrigen"].bind().shouldBeInstanceOf<JsonString>()
            body["peticion"]["cdgoEstDestino"].bind().shouldBeInstanceOf<JsonString>()
            body["horario"].bind().shouldBeInstanceOf<JsonArray>()
        }.getOrElse { throw it }
    }

    @Test
    fun `should not get stop times`() = testApplicationBusTracker {
        val url = "/stops/train/times?originStopCode=asdasd&destinationStopCode=asdasd"
        val response = client.get(url)
        response.status shouldBe HttpStatusCode.NotFound
    }
}