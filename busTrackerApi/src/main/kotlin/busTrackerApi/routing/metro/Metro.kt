package busTrackerApi.routing.metro

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import simpleJson.*

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

    return httpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return@use null
        val array = response.body?.string()?.deserialized()?.get("Vtelindicadores")?.asArray()?.getOrNull()
        return jObject {
            "nombre_estacion" += array?.get(0)?.get("nombreest")?.asString()?.getOrNull() ?: return@use null
            "times" += jArray {
                //TODO Replace with nullable assigns when simpleJson supports it
                array?.forEach {
                    addObject {
                        "linea" += it["linea"].asNumber().getOrNull() ?: return@forEach
                        "anden" += it["anden"].asNumber().getOrNull() ?: return@forEach
                        "sentido" += it["sentido"].asString().getOrNull() ?: return@forEach
                        "proximo" += it["proximo"].asNumber().getOrNull() ?: return@forEach
                        "siguiente" += it["siguiente"].asNumber().getOrNull() ?: return@forEach
                    }
                }
            }
        }
    }
}