package busTrackerApi.routing.abono

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.timeoutSeconds
import busTrackerApi.utils.Call
import crtm.abonoClient
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

val xml = XML { autoPolymorphic = true }

suspend fun Call.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val response = withTimeoutOrNull(timeoutSeconds) {
        CoroutineScope(Dispatchers.IO).async { abonoClient.consultaSaldo1(id) }.await()
    } ?: shift<Nothing>(SoapError("No locations found for line $id"))

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(response.sResulXMLField)
    isFound(result).bind()

    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}