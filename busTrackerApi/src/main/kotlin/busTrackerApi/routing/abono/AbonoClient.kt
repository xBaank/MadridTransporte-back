package busTrackerApi.routing.abono

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.post
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Pipeline
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import ru.gildor.coroutines.okhttp.await

val xml = XML { autoPolymorphic = true }

suspend fun Pipeline.getAbono() = either {
    val id = call.parameters.getWrapped("id").bind()
    val response =
        httpClient.post(
            "https://serviciosapp.metromadrid.es/tarjetapost/login",
            "sNumeroTP=$id&version=2",
            headers = mapOf(
                "Host" to "serviciosapp.metromadrid.es"
            ),
            contentType = "application/x-www-form-urlencoded"
        ).await()

    if (!response.isSuccessful) shift<Nothing>(InternalServerError("Can't get abono data"))

    val data: String = response.body?.string() ?: shift<Nothing>(InternalServerError("Can't get abono data"))

    val result = xml.decodeFromString<SS_prepagoConsultaSaldo>(data)
    isFound(result).bind()

    call.caching = CachingOptions(cacheControl = CacheControl.MaxAge(maxAgeSeconds = 30))
    ResponseJson(buildAbonoJson(result), HttpStatusCode.OK)
}