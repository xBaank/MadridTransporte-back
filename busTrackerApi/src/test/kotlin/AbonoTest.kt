import arrow.core.getOrElse
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import simpleJson.JsonArray
import simpleJson.JsonObject
import simpleJson.deserialized
import simpleJson.get
import utils.getAbono
import utils.testApplicationBusTracker

const val abonoJoven = "0000000010040117584"
const val abonoMetro10Viajes = "0000000010040117583"
const val abonoNormal = "2222222510010656361"
const val abonoTerceraEdad = "0000000010040117155"

class AbonoTest {
    @ParameterizedTest
    @ValueSource(strings = [abonoJoven, abonoMetro10Viajes, abonoNormal, abonoTerceraEdad])
    fun `should get abono`(id: String) = testApplicationBusTracker {
        val result = getAbono(id)

        val json = result.bodyAsText().deserialized().getOrElse { throw it }

        result.status.shouldBe(HttpStatusCode.OK)
        json.shouldBeInstanceOf<JsonObject>()
        json["contracts"].getOrElse { throw it }.shouldBeInstanceOf<JsonArray>()
    }

    @Test
    fun `should not get abono`() = testApplicationBusTracker {
        val result = getAbono("asdasd")
        result.status.shouldBe(HttpStatusCode.NotFound)
    }
}