package busTrackerApi.routing.abono

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.SoapError
import busTrackerApi.extensions.getSuspend
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import busTrackerApi.utils.abonoClient
import busTrackerApi.utils.mapExceptionsF
import busTrackerApi.utils.timeoutSeconds
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

val xml = XML { autoPolymorphic = true }

suspend fun Call.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val response = withTimeoutOrNull(timeoutSeconds) {
        Either.catch {
            getSuspend(id, abonoClient.value()::consultaSaldo1Async)
        }.mapLeft(mapExceptionsF)
    }?.bind() ?: shift<Nothing>(SoapError("Server error"))

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(response.consultaSaldo1Result.value.sResulXMLField)
    isFound(result).bind()

    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}