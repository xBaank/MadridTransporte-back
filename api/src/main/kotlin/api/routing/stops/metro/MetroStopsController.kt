package api.routing.stops.metro

import api.config.httpClient
import api.db.getCoordinatesByStopCode
import api.db.getIdByStopCode
import api.db.getStopNameById
import api.exceptions.BusTrackerException
import api.extensions.bindJson
import arrow.core.continuations.either
import arrow.core.getOrElse
import crtm.utils.getStopCodeFromFullStopCode
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import simpleJson.asArray
import simpleJson.deserialized
import simpleJson.get
import simpleJson.jArray

private fun urlBuilder() = HttpUrl.Builder()
    .scheme("https")
    .host("serviciosapp.metromadrid.es")
    .addPathSegment("servicios")
    .addPathSegment("rest")
    .addPathSegment("teleindicadores")

private suspend fun getMetroTimesResponse(id: String? = null): Response {
    val url = urlBuilder()
        .also { if (id != null) it.addPathSegment(id) }
        .build()

    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build()

    return httpClient.newCall(request).await()
}

suspend fun getMetroTimes(fullStopCode: String, codMode: String) = either {
    val codigoEmpresa = getIdByStopCode(fullStopCode).bind()
    val response = getMetroTimesResponse(codigoEmpresa)
    val coordinates = getCoordinatesByStopCode(fullStopCode).bind()

    response.use {
        if (it.code == 404) shift<BusTrackerException.NotFound>(BusTrackerException.NotFound("Station not found"))
        if (it.code in 500..600) shift<BusTrackerException.InternalServerError>(
            BusTrackerException.InternalServerError(
                "Internal server error"
            )
        )
        val body = it
            .body
            .use { it?.string() }

        val json = body?.deserialized()
            ?.get("Vtelindicadores")
            ?.asArray()
            ?.getOrElse { jArray() }
            ?: jArray()

        extractMetroStopTimes(
            json,
            codMode,
            coordinates,
            getStopNameById(codigoEmpresa).bind(),
            getStopCodeFromFullStopCode(fullStopCode)
        ).bindJson()
    }
}