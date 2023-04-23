package busTrackerApi.routing.metro

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import simpleJson.JsonNode
import simpleJson.deserialized

val httpClient = OkHttpClient.Builder().build()
private val urlBuilder = HttpUrl.Builder()
    .scheme("https")
    .host("serviciosapp.metromadrid.es")
    .addPathSegment("servicios")
    .addPathSegment("rest")
    .addPathSegment("teleindicadores")

fun getTimes(id: String? = null): JsonNode? {
    val url = urlBuilder
        .also { if (id != null) it.addPathSegment(id) }
        .build()


    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build()

    val response = httpClient.newCall(request).execute()
    if (!response.isSuccessful) return null
    return response.body?.source()?.deserialized()?.getOrNull()
}