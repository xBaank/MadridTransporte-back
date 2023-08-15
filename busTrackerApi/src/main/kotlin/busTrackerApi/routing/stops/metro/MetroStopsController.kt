package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import arrow.core.getOrElse
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildJson
import busTrackerApi.routing.stops.getStopNameById
import busTrackerApi.routing.stops.timed
import io.github.reactivecircus.cache4k.Cache
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await
import simpleJson.*
import kotlin.time.Duration.Companion.hours

private val cache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

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

suspend fun getTimesByQuery(id: String, codMode: String) = either {
    val response = getTimesBase(id, codMode).bind().timed()
    cache.put(id, response)
    response
}

suspend fun getTimesByQueryCached(id: String, codMode: String) = either {
    cache.get(id) ?: shift<Nothing>(BusTrackerException.NotFound("No stops found for query $id"))
}

suspend fun getTimesBase(id: String, codMode: String) = either {
    val response = getMetroTimesResponse(id)

    response.use {
        if (it.code == 404) shift<BusTrackerException.NotFound>(BusTrackerException.NotFound("Station not found"))
        if (it.code in 500..600) shift<BusTrackerException.InternalServerError>(
            BusTrackerException.InternalServerError(
                "Internal server error"
            )
        )
        val body = it.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))

        val json = body.deserialized()
            .get("Vtelindicadores")
            .asArray()
            .getOrElse { jArray() }

        parseMetroToStopTimes(json, codMode)
            .bindMap()
            .copy(stopName = getStopNameById(id).bind()) //When no times are available, the stop name is not returned, so we need to get it from the stops list
            .let(::buildJson)
    }
}