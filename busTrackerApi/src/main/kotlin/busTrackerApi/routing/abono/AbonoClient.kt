package busTrackerApi.routing.abono

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.timeoutSeconds
import busTrackerApi.utils.Call
import busTrackerApi.utils.abonoClient
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

val xml = XML { autoPolymorphic = true }

suspend fun Call.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val response = withTimeoutOrNull(timeoutSeconds) {
        withContext(Dispatchers.IO) { abonoClient.value().consultaSaldo1(id) }
    } ?: shift<Nothing>(SoapError("No locations found for line $id"))

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(response.sResulXMLField)
    isFound(result).bind()

    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}