package api.routing.abono

import api.config.httpClient
import api.exceptions.BusTrackerException.InternalServerError
import api.extensions.awaitWrap
import api.extensions.post
import arrow.core.Either
import arrow.core.raise.either
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML

val xml = XML { autoPolymorphic = true }

suspend fun getAbonoResponse(TTPNumber: String) = either {
    val response = httpClient.post(
        "https://serviciosapp.metromadrid.es/tarjetapost/login",
        "sNumeroTP=$TTPNumber&version=2",
        headers = mapOf(
            "Host" to "serviciosapp.metromadrid.es"
        ),
        contentType = "application/x-www-form-urlencoded"
    ).awaitWrap().bind()

    if (!response.isSuccessful) raise(InternalServerError("Can't get abono data"))

    val data: String = response.use { it.body?.string() ?: raise(InternalServerError("Can't get abono data")) }

    val result = Either.catch { xml.decodeFromString<SS_prepagoConsultaSaldo>(data) }
        .mapLeft { InternalServerError(it.message) }
        .bind()

    extractAbono(result).bind()
}