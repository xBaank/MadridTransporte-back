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
        jArray {
            array?.forEach {
                addObject {
                    "id" += it["idnumerica"].asNumber().getOrNull()
                    "nombre_estacion" += it["nombreest"].asString().getOrNull()
                    "linea" += it["linea"].asNumber().getOrNull()
                    "anden" += it["anden"].asNumber().getOrNull()
                    "sentido" += it["sentido"].asString().getOrNull()
                    val proximo = it["proximo"].asNumber().getOrNull()
                    val siguiente = it["siguiente"].asNumber().getOrNull()
                    val proximos = listOfNotNull(proximo, siguiente).map(Number::asJson).asJson()
                    "proximos" += proximos
                }
            }
        }
    }
}