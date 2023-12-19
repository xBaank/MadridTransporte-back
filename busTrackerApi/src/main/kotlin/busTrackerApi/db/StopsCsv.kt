package busTrackerApi.db

import busTrackerApi.db.models.Itinerary
import busTrackerApi.db.models.Stop
import busTrackerApi.db.models.StopInfo
import busTrackerApi.db.models.StopOrder
import crtm.utils.createStopCode

fun parseStops(data: Map<String, String>): Stop {
    val codMode = data["stop_id"]?.substringAfter("_")?.substringBefore("_")?.toInt() ?: 0
    val stopCode = data["stop_code"].toString().trim()
    return Stop(
        stopCode = stopCode,
        stopName = data["stop_name"].toString(),
        stopLat = data["stop_lat"]?.toDoubleOrNull() ?: 0.0,
        stopLon = data["stop_lon"]?.toDoubleOrNull() ?: 0.0,
        codMode = codMode,
        fullStopCode = createStopCode(codMode.toString(), stopCode)
    )
}

fun parseStopsInfo(data: Map<String, String>): StopInfo = StopInfo(
    idEstacion = data["IDESTACION"].toString(),
    codigoEmpresa = data["CODIGOEMPRESA"].toString()
)

fun parseItineraries(data: Map<String, String>) = Itinerary(
    itineraryCode = data["shape_id"].toString(),
    direction = data["direction_id"]?.toIntOrNull() ?: 0,
    fullLineCode = data["route_id"].toString(),
    tripId = data["trip_id"].toString()
)

fun parseStopsOrder(data: Map<String, String>) = StopOrder(
    fullStopCode = data["stop_id"].toString().removePrefix("par_"),
    order = data["stop_sequence"]?.toIntOrNull() ?: 0,
    tripId = data["trip_id"].toString()
)