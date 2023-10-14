package busTrackerApi.db

import arrow.core.continuations.either
import busTrackerApi.extensions.bindMap
import simpleJson.*

suspend fun parseStops(json: JsonArray) = either {
    json.map {
        Stop(
            stopCode = it["stop_code"].asString().bindMap(),
            stopName = it["stop_name"].asString().bindMap(),
            stopLat = it["stop_lat"].asDouble().bindMap(),
            stopLon = it["stop_lon"].asDouble().bindMap(),
            codMode = it["cod_mode"].asInt().bindMap(),
            fullStopCode = it["full_stop_code"].asString().bindMap()
        )
    }
}

suspend fun parseStopsInfo(json: JsonArray) = either {
    json.map {
        StopsInfo(
            idEstacion = it["IDESTACION"].asString().bindMap(),
            codigoEmpresa = it["CODIGOEMPRESA"].asString().bindMap(),
        )
    }
}

fun buildStopJson(stop: Stop) = jObject {
    "stopCode" += stop.stopCode
    "stopName" += stop.stopName
    "stopLat" += stop.stopLat
    "stopLon" += stop.stopLon
    "codMode" += stop.codMode
    "fullStopCode" += stop.fullStopCode
}

fun buildStopsInfoJson(stop: StopsInfo) = jObject {
    "idEstacion" += stop.idEstacion
    "codigoEmpresa" += stop.codigoEmpresa
}