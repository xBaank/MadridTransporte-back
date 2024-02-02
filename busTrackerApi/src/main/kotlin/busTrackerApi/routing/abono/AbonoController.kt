package busTrackerApi.routing.abono

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.post
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import ru.gildor.coroutines.okhttp.await

val xml = XML { autoPolymorphic = true }

suspend fun getAbonoResponse(TTPNumber: String) = either {
    val response = httpClient.post(
        "https://serviciosapp.metromadrid.es/tarjetapost/login",
        "sNumeroTP=$TTPNumber&version=2",
        headers = mapOf(
            "Host" to "serviciosapp.metromadrid.es"
        ),
        contentType = "application/x-www-form-urlencoded"
    ).await()

    if (!response.isSuccessful) shift<Nothing>(BusTrackerException.InternalServerError("Can't get abono data"))

    val data: String =
        response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Can't get abono data"))

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(data)
    isFound(result).bind()
    result
}