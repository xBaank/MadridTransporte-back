package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.removeNonSpacingMarks
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.timed
import io.github.reactivecircus.cache4k.Cache
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
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

fun getMetroTimesResponse(id: String? = null): Response {
    val url = urlBuilder()
        .also { if (id != null) it.addPathSegment(id) }
        .build()

    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build()

    return httpClient.newCall(request).execute()
}

suspend fun getTimesByQuery(query: String) = either {
    val response = getTimesBase(query).bind().timed()
    cache.put(query, response)
    response
}

suspend fun getTimesByQueryCached(query: String) = either {
    cache.get(query) ?: shift<Nothing>(BusTrackerException.NotFound("No stops found for query $query"))
}

suspend fun getTimesBase(filter: String, id: String? = null) = either {
    val response = getMetroTimesResponse(id)

    response.use { it ->
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
            .bindMap()

        val filtered = json.filter {
            it["nombreest"].asString()
                .bindMap()
                .removeNonSpacingMarks()
                .contains(filter.removeNonSpacingMarks(), true)
        }.asJson()

        if(filtered.isEmpty()) shift<BusTrackerException.NotFound>(BusTrackerException.NotFound("Station not found"))

        buildMetroJson(filtered)
    }
}