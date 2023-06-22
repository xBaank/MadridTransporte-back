package busTrackerApi.routing.abono

import crtm.abonoClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

fun Route.abonoRouting() {
    get("{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val response = CoroutineScope(Dispatchers.IO).async { abonoClient.consultaSaldo1(id) }.await()
        val xml = XML {
            // configuration options
            autoPolymorphic = true
        }
        val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(response.sResulXMLField)
        println(result)
    }
}