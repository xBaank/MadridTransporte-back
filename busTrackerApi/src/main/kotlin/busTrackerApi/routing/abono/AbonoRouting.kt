package busTrackerApi.routing.abono

import busTrackerApi.utils.errorObject
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
import simpleJson.serialized

val xml = XML {
    autoPolymorphic = true
}

fun Route.abonoRouting() {
    get("{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            errorObject("Missing id").serialized(),
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.BadRequest
        )
        val response = CoroutineScope(Dispatchers.IO).async { abonoClient.consultaSaldo1(id) }.await()

        val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(response.sResulXMLField)

        if (!isFound(result)) return@get call.respondText(
            errorObject("No se ha encontrado el abono").serialized(),
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.NotFound
        )

        return@get call.respondText(
            buildAbonoJson(result).serialized(),
            contentType = ContentType.Application.Json,
            status = HttpStatusCode.OK
        )
    }
}