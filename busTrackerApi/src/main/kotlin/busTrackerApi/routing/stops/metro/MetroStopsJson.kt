package busTrackerApi.routing.stops.metro

import simpleJson.*

fun buildMetroJson(array: JsonArray) = jObject {
    "name" += array.firstOrNull()?.get("nombreest")?.asString()?.getOrNull()
    "times" += jArray {
        array.forEach {
            addObject {
                "id" += it["idnumerica"].asNumber().getOrNull()
                "idTeleindicador" += it["estaciontel"].asNumber().getOrNull()
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