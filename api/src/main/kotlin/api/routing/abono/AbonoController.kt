package api.routing.abono

import api.config.httpClient
import api.exceptions.BusTrackerException.InternalServerError
import api.extensions.post
import arrow.core.raise.either
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

    if (!response.isSuccessful) raise(InternalServerError("Can't get abono data"))

    val data: String = response.use { it.body?.string() ?: raise(InternalServerError("Can't get abono data")) }

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(data)
    extractAbono(result).bind()
}