package modes

import crtm.CRTMClient
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class ModesClientTest {
    @Test
    fun `should get modes`(): Unit = runBlocking {
        val client = CRTMClient()

        val modes = client.modes.getModes()

        modes.shouldNotBeEmpty()
        modes shouldContain Mode(CodMode(4), "METRO")
        modes shouldContain Mode(CodMode(8), "AUTOBUSES INTERURBANOS")
        modes shouldContain Mode(CodMode(5), "CERCANIAS")
        modes shouldContain Mode(CodMode(10), "METRO LIGERO/TRANVÍA")
        modes shouldContain Mode(CodMode(6), "AUTOBUSES EMT")
    }
}