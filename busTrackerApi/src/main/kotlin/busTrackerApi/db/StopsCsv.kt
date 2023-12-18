package busTrackerApi.db

import busTrackerApi.db.models.Itinerary
import busTrackerApi.db.models.Stop
import busTrackerApi.db.models.StopInfo
import busTrackerApi.db.models.StopOrder

fun parseStops(data: Map<String, String>) = Stop(
    stopCode = data["stop_code"].toString(),
    stopName = data["stop_name"].toString(),
    stopLat = data["stop_lat"]?.toDoubleOrNull() ?: 0.0,
    stopLon = data["stop_lon"]?.toDoubleOrNull() ?: 0.0,
    codMode = data["cod_mode"]?.toIntOrNull() ?: 0,
    fullStopCode = data["full_stop_code"].toString()
)

fun parseStopsInfo(data: Map<String, String>): StopInfo {
    val codigoAnden = data["CODIGOANDENEMPRESA"].toString()
    return StopInfo(
        idEstacion = data["IDFESTACION"].toString(),
        codigoEmpresa = codigoAnden.removeRange(codigoAnden.length - 1, codigoAnden.length),
    )
}

fun parseItineraries(data: Map<String, String>) = Itinerary(
    itineraryCode = data["shape_id"].toString(),
    direction = data["direction_id"]?.toIntOrNull() ?: 0,
    fullLineCode = data["route_id"].toString(),
    tripId = data["trip_id"].toString()
)

fun parseStopsOrder(data: Map<String, String>) = StopOrder(
    fullStopCode = data["stop_id"].toString(),
    order = data["stop_sequence"]?.toIntOrNull() ?: 0,
    tripId = data["trip_id"].toString()
)