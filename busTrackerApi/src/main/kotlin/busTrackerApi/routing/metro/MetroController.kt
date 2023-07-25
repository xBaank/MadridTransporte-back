package busTrackerApi.routing.metro

import arrow.core.continuations.either
import busTrackerApi.extensions.toBusTrackerException
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import simpleJson.*

val httpClient = OkHttpClient.Builder().build()

fun urlBuilder() = HttpUrl.Builder()
    .scheme("https")
    .host("serviciosapp.metromadrid.es")
    .addPathSegment("servicios")
    .addPathSegment("rest")
    .addPathSegment("teleindicadores")

suspend fun buildMetroJson(value : String) = either {
    val array = value.deserialized()
        .get("Vtelindicadores")
        .asArray()
        .toBusTrackerException()
        .bind()

    jArray {
        array.forEach {
            addObject {
                "id" += it["idnumerica"].asNumber().getOrNull()
                "nombreEstacion" += it["nombreest"].asString().getOrNull()
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