package busTrackerApi.routing.metro

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.routing.Response.ResponseJson
import io.ktor.http.*
import okhttp3.Request

suspend fun getTimes(id: String? = null) = either {
    val url = urlBuilder()
        .also { if (id != null) it.addPathSegment(id) }
        .build()

    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build()

    val json = httpClient.newCall(request).execute().use { response ->
        if (response.code == 404) shift<NotFound>(NotFound("Station not found"))
        if (response.code in 500..600) shift<InternalServerError>(InternalServerError("Internal server error"))
        buildMetroJson(response.body?.string() ?: shift(InternalServerError("Got empty response")))
    }.bind()

    ResponseJson(json, HttpStatusCode.OK)
}