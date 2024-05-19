package api.routing.stops.metro

import api.config.httpClient
import arrow.core.raise.either
import common.exceptions.BusTrackerException.NotFound
import common.extensions.awaitWrap
import common.extensions.bindJson
import common.queries.getCoordinatesByStopCode
import common.queries.getIdByStopCode
import common.queries.getStopNameById
import common.utils.getStopCodeFromFullStopCode
import common.utils.metroCodMode
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
        if (it?.code == 404) raise(NotFound("Station not found"))

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