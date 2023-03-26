package stops

import crtm.CRTMClient
import kotlinx.coroutines.runBlocking
import lines.CodLine
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class StopsClientTest {
    @Test
    fun `should get stops by CodStop`(): Unit = runBlocking {
        val client = CRTMClient()
        val stop = client.stops.getStopInfoByCodStop(CodStop("8_07904"))

        stop.name shouldBeEqualTo "AV.UNIVERSIDAD-POLICÍA NACIONAL"
        stop.codStop shouldBeEqualTo CodStop("8_07904")
        stop.coordinates.latitude shouldBeEqualTo 40.330936319115
        stop.coordinates.longitude shouldBeEqualTo -3.7655800899764
        stop.lines shouldContain CodLine("8__450___")
    }

    @Test
    fun `should get stops by search`(): Unit = runBlocking {
        val client = CRTMClient()
        val stops = client.stops.getStopsBySearch("Universidad")
        stops.shouldNotBeEmpty()
        stops.any { it.name == "AV.UNIVERSIDAD-POLICÍA NACIONAL" }.shouldBeTrue()
    }

    @Test
    fun `should get stops times by CodStop`(): Unit = runBlocking {
        val client = CRTMClient()
        val stops = client.stops.getStopsTime(CodStop("8_08242"))
        stops.shouldNotBeEmpty()
    }
}