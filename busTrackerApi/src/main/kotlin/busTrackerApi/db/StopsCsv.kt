package busTrackerApi.db

import busTrackerApi.db.models.*
import crtm.utils.createStopCode

fun parseStop(data: Map<String, String>): Stop {
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

fun parseRoute(data: Map<String, String>): Route = Route(
    fullLineCode = data["route_id"].toString(),
    simpleLineCode = data["route_short_name"].toString(),
    routeName = data["route_long_name"].toString(),
    codMode = data["route_id"].toString().substringBefore("_")
)

fun parseStopInfo(data: Map<String, String>): StopInfo = StopInfo(
    idEstacion = data["IDESTACION"].toString(),
    codigoEmpresa = data["CODIGOEMPRESA"].toString()
)

fun parseItinerary(data: Map<String, String>) = Itinerary(
    itineraryCode = data["shape_id"].toString(),
    direction = data["direction_id"]?.toIntOrNull() ?: 0,
    fullLineCode = data["route_id"].toString(),
    tripId = data["trip_id"].toString()
)

fun parseShape(data: Map<String, String>) = Shape(
    itineraryId = data["shape_id"].toString(),
    latitude = data["shape_pt_lat"]?.toDoubleOrNull() ?: 0.0,
    longitude = data["shape_pt_lon"]?.toDoubleOrNull() ?: 0.0,
    sequence = data["shape_pt_sequence"]?.toIntOrNull() ?: 0
)

fun parseStopsOrder(data: Map<String, String>) = StopOrder(
    fullStopCode = data["stop_id"].toString().removePrefix("par_"),
    order = data["stop_sequence"]?.toIntOrNull() ?: 0,
    tripId = data["trip_id"].toString()
)