package busTrackerApi.db

import busTrackerApi.db.models.Itinerary
import busTrackerApi.db.models.Stop
import busTrackerApi.db.models.StopsInfo

fun parseStops(data: Map<String, String>) = Stop(
    stopCode = data["stop_code"].toString(),
    stopName = data["stop_name"].toString(),
    stopLat = data["stop_lat"]!!.toDouble(),
    stopLon = data["stop_lon"]!!.toDouble(),
    codMode = data["cod_mode"]!!.toInt(),
    fullStopCode = data["full_stop_code"].toString()
)

fun parseStopsInfo(data: Map<String, String>) = StopsInfo(
    idEstacion = data["IDESTACION"].toString(),
    codigoEmpresa = data["CODIGOEMPRESA"].toString(),
)

fun parseItineraries(data: Map<String, String>) = Itinerary(
    itineraryCode = data["shape_id"].toString(),
    direction = data["direction_id"]!!.toInt(),
    fullLineCode = data["route_id"].toString(),
    tripId = data["trip_id"].toString()
)