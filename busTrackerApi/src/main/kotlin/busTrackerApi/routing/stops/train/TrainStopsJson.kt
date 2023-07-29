package busTrackerApi.routing.stops.train

import simpleJson.*

fun buildTrainJson(jsonNode: JsonArray) = jObject {
    "name" += jsonNode.firstOrNull()?.get("peticion")?.get("descEstOrigen")?.asString()?.getOrNull()
    "codMode" += trainCodMode
    "times" += jsonNode.map {
        jObject {
            "destination" += it["peticion"]["descEstDestino"].asString().getOrNull()
            "horarios" += it["horario"].asArray().getOrNull()?.asJson() ?: JsonNull
        }
    }.asJson()
}