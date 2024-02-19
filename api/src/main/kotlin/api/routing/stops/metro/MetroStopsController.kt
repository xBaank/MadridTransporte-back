package api.routing.stops.metro

import api.config.httpClient
import api.db.getCoordinatesByStopCode
import api.db.getIdByStopCode
import api.db.getStopNameById
import api.exceptions.BusTrackerException.NotFound
import api.extensions.awaitWrap
import api.extensions.bindJson
import arrow.core.continuations.either
import crtm.utils.getStopCodeFromFullStopCode
import okhttp3.HttpUrl
import okhttp3.Request
import simpleJson.asArray
import simpleJson.deserialized
import simpleJson.get

private fun urlBuilder() = HttpUrl.Builder()
    .scheme("https")
    .host("serviciosapp.metromadrid.es")
    .addPathSegment("servicios")
    .addPathSegment("rest")
    .addPathSegment("teleindicadores")

private suspend fun getMetroTimesResponse(id: String? = null) = either {
    val url = urlBuilder()
        .also { if (id != null) it.addPathSegment(id) }
        .build()

    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build()

    httpClient.newCall(request).awaitWrap().bind()
}

suspend fun getMetroTimes(fullStopCode: String) = either {
    val codigoEmpresa = getIdByStopCode(fullStopCode).bind()
    val response = getMetroTimesResponse(codigoEmpresa).getOrNull()
    val coordinates = getCoordinatesByStopCode(fullStopCode).bind()

    response.use {
        if (it?.code == 404) shift<NotFound>(NotFound("Station not found"))

        val json = it?.body
            ?.string()
            ?.deserialized()
            ?.get("Vtelindicadores")
            ?.asArray()
            ?.getOrNull()

        extractMetroStopTimes(
            json,
            metroCodMode,
            coordinates,
            getStopNameById(codigoEmpresa).bind(),
            getStopCodeFromFullStopCode(fullStopCode)
        ).bindJson()
    }
}