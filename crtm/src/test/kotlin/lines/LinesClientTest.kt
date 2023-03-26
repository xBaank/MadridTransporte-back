package lines

import crtm.CRTMClient
import crtm.privateKey
import crtm.soap.*
import crtm.utils.authHeader
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class LinesClientTest {
    @Test
    fun `get lines by CodLine should return LineInfo`(): Unit = runBlocking {
        val client = MultimodalInformation_Service()
        val key = client.basicHttp.getPublicKey(PublicKeyRequest())

        val auth = authHeader(key.key.toByteArray(), privateKey)
        val times = client.basicHttp.getStopTimes(StopTimesRequest().apply {
            authentication = auth
            codStop = "8_07904"
        })

        times.stopTimes.times.time.shouldNotBeEmpty()


        val info = client.basicHttp.getLineItineraries(LineItineraryRequest().apply {
            authentication = auth
            codLine = "8__450___"
            active = 1
        })

        info.itineraries.lineItinerary.shouldNotBeEmpty()

        val locations = client.basicHttp.getLineLocation(LineLocationRequest().apply {
            authentication = auth
            codLine = "8__450___"
            codMode = "8"
            codStop = "8_07904"
            val itinerary = info.itineraries.lineItinerary.first()
            direction = itinerary.direction
            codItinerary = itinerary.codItinerary
        })

        locations.vehiclesLocation.vehicleLocation.shouldNotBeEmpty()
        println(locations)

       /* val client = CRTMClient()
        val line = client.lines.getLineInfoByCodLine(CodLine("8__450___"))
        line.codLine shouldBeEqualTo CodLine("8__450___")
        line.itineraries shouldHaveSize 2
        line.itineraries.firstOrNull()?.stops?.shouldNotBeEmpty()*/
    }

    @Test
    fun `get line location by CodLine should return LineLocation`(): Unit = runBlocking {
        val client = CRTMClient()
        val linesLocations = client.lines.getLineLocationByCodLine(CodLine("8__450___")).take(4).toList()
        linesLocations.shouldNotBeEmpty()
        linesLocations.firstOrNull()?.codLine shouldBeEqualTo CodLine("8__450___")
    }
}