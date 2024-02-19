import api.extensions.getOrThrow
import arrow.core.continuations.either
import arrow.core.getOrElse
import io.ktor.client.statement.*
import io.ktor.http.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNullOrEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import simpleJson.*
import utils.getAbono
import utils.testApplicationBusTracker

const val abonoJoven = "0010000000010040117584"
const val abonoMetro10Viajes = "0010000000010040117583"
const val abonoNormal = "0012222222510010656361"
const val abonoTerceraEdad = "0010000000010040117155"

class AbonoTest {
    @ParameterizedTest
    @ValueSource(strings = [abonoJoven, abonoMetro10Viajes, abonoNormal, abonoTerceraEdad])
    fun `should get abono`(id: String) = testApplicationBusTracker {
        val result = getAbono(id)

        val json = result.bodyAsText().deserialized().getOrElse { throw it }

        either {
            result.status.shouldBe(HttpStatusCode.OK)
            json.shouldBeInstanceOf<JsonObject>()
            json["serialNumber"].asString().bind().shouldNotBeNullOrEmpty()
            json["ttpNumber"].asString().bind().shouldBeEqualTo(id)
            json["createdAt"].asString().bind().shouldNotBeNullOrEmpty()
            json["expireAt"].asString().bind().shouldNotBeNullOrEmpty()
            json["contracts"].asArray().bind().forEach {
                it["contractCode"].asInt().bind()
                it["contractName"].asString().bind()
                it["contractCompanyPropietary"].asInt().bind()
                it["contractUserProfileType"].asString().bind()
                it["contractUserProfilePropietaryCompany"].asString().bind()
                it["chargeDate"].asString().bind()
                it["firstUseDateLimit"].bind()
                it["firstUseDate"].bind()
                it["useDays"].bind()
                it["leftDays"].bind()
                it["charges"].asInt().bind()
                it["remainingCharges"].asInt().bind()
            }
        }.getOrThrow()
    }

    @Test
    fun `should not get abono`() = testApplicationBusTracker {
        val result = getAbono("asdasd")
        result.status.shouldBe(HttpStatusCode.NotFound)
    }
}