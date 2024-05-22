package loader

import common.models.*
import common.utils.createStopCode
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val logger = LoggerFactory.getLogger("StopsCsv")

fun parseStop(data: Map<String, String>): Stop? = runCatching {
    val codMode = data["stop_id"]?.substringAfter("_")?.substringBefore("_")?.toInt() ?: 0
    val stopCode = data["stop_code"].toString().trim()
    Stop(
        stopCode = stopCode,
        stopName = data["stop_name"].toString(),
        stopLat = data["stop_lat"]?.toDoubleOrNull() ?: 0.0,
        stopLon = data["stop_lon"]?.toDoubleOrNull() ?: 0.0,
        codMode = codMode,
        fullStopCode = createStopCode(codMode.toString(), stopCode),
        wheelchair = data["wheelchair_boarding"]?.toIntOrNull() ?: 0,
        zone = data["zone_id"].toString()
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()

fun parseRoute(data: Map<String, String>): Route? = runCatching {
    Route(
        fullLineCode = data["route_id"].toString(),
        simpleLineCode = data["route_short_name"].toString(),
        routeName = data["route_long_name"].toString(),
        codMode = data["route_id"].toString().substringBefore("_")
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()

fun parseStopInfo(data: Map<String, String>): StopInfo? = runCatching {
    StopInfo(
        idEstacion = data["IDESTACION"].toString(),
        codigoEmpresa = data["CODIGOEMPRESA"].toString()
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()

fun parseItinerary(data: Map<String, String>) = runCatching {
    Itinerary(
        itineraryCode = data["shape_id"].toString(),
        direction = data["direction_id"]?.toIntOrNull() ?: 0,
        fullLineCode = data["route_id"].toString(),
        tripId = data["trip_id"].toString(),
        serviceId = data["service_id"].toString(),
        tripName = data["trip_short_name"].toString()
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()

fun parseShape(data: Map<String, String>): Shape? = runCatching {
    Shape(
        itineraryId = data["shape_id"].toString(),
        latitude = data["shape_pt_lat"]?.toDoubleOrNull() ?: 0.0,
        longitude = data["shape_pt_lon"]?.toDoubleOrNull() ?: 0.0,
        sequence = data["shape_pt_sequence"]?.toIntOrNull() ?: 0,
        distance = data["shape_dist_traveled"]?.toDoubleOrNull() ?: 0.0
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()

fun parseStopsOrder(data: Map<String, String>): StopOrder? = runCatching {
    val departureTime = data["departure_time"].toString()
    val times = departureTime.split(":")
    val hour = times[0].toInt()
    var daysToAdd = 0L
    val fixedHour = if (hour >= 24) {
        hour - 24
        daysToAdd++
    } else hour
    val newTime = listOf(fixedHour.toString().padStart(2, '0'), times[1], times[2]).joinToString(":")
    val departure = LocalTime.parse(newTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
        .atDate(LocalDate.EPOCH)
        .atZone(ZoneOffset.UTC)
        .plusDays(daysToAdd)
        .toInstant()
        .toEpochMilli()

    StopOrder(
        fullStopCode = data["stop_id"].toString().removePrefix("par_"),
        order = data["stop_sequence"]?.toIntOrNull() ?: 0,
        tripId = data["trip_id"].toString(),
        departureTime = departure
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()

private val format = SimpleDateFormat("yyyyMMdd")
fun parseCalendar(data: Map<String, String>): Calendar? = runCatching {
    Calendar(
        serviceId = data["service_id"].toString(),
        monday = data["monday"]?.toInt() == 1,
        tuesday = data["tuesday"]?.toInt() == 1,
        wednesday = data["wednesday"]?.toInt() == 1,
        thursday = data["thursday"]?.toInt() == 1,
        friday = data["friday"]?.toInt() == 1,
        saturday = data["saturday"]?.toInt() == 1,
        sunday = data["sunday"]?.toInt() == 1,
        startDate = format.parse(data["start_date"].toString()).toInstant().toEpochMilli(),
        endDate = format.parse(data["end_date"].toString()).toInstant().toEpochMilli()
    )
}
    .onFailure { logger.error(it.message, it) }
    .getOrNull()