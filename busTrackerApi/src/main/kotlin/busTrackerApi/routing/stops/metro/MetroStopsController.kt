package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import arrow.core.getOrElse
import busTrackerApi.config.httpClient
import busTrackerApi.db.getCoordinatesByStopCode
import busTrackerApi.db.getIdByStopCode
import busTrackerApi.db.getStopCodeById
import busTrackerApi.db.getStopNameById
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindJson
import crtm.utils.getCodStopFromStopCode
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import simpleJson.asArray
import simpleJson.deserialized
import simpleJson.get
import simpleJson.jArray

fun urlBuilder() = HttpUrl.Builder()
    .scheme("https")
    .host("serviciosapp.metromadrid.es")
    .addPathSegment("servicios")
    .addPathSegment("rest")
    .addPathSegment("teleindicadores")

suspend fun getMetroTimesResponse(id: String? = null): Response {
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

suspend fun getMetroTimesResponse(id: String, codMode: String) = either {
    val stationCode = getIdByStopCode(id).bind()
    val response = getTimesBase(stationCode, codMode).bind()
    response
}

suspend fun getTimesBase(id: String, codMode: String) = either {
    val response = getMetroTimesResponse(id)
    val stopCode = getStopCodeById(id).bind()
    val coordinates = getCoordinatesByStopCode(stopCode).bind()

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

        parseMetroToStopTimes(json, codMode, coordinates, getStopNameById(id).bind(), getCodStopFromStopCode(stopCode))
            .bindJson()
    }
}