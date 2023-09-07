package busTrackerApi.routing.abono

import arrow.core.continuations.either
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.wait
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.abonoClient
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

val xml = XML { autoPolymorphic = true }

suspend fun Call.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val response = abonoClient.consultaSaldo1Async(id).wait().consultaSaldo1Result.value

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(response.sResulXMLField)
    isFound(result).bind()

    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}