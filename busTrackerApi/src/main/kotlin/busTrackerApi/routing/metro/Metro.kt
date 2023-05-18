package busTrackerApi.routing.metro

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import simpleJson.JsonNode
import simpleJson.deserialized
import simpleJson.get

val httpClient = OkHttpClient.Builder().build()
fun urlBuilder() = HttpUrl.Builder()
    .scheme("https")
    .host("serviciosapp.metromadrid.es")
    .addPathSegment("servicios")
    .addPathSegment("rest")
    .addPathSegment("teleindicadores")

fun getTimes(id: String? = null): JsonNode? {
    val url = urlBuilder()
        .also { if (id != null) it.addPathSegment(id) }
        .build()


    val request = Request.Builder()
        .url(url)
        .get()
        .addHeader("Accept", "application/json")
        .build()

    return httpClient.newCall(request).execute().use {
        if (!it.isSuccessful) null
        else it.body?.string()?.deserialized()?.get("Vtelindicadores")?.getOrNull()
    }
}