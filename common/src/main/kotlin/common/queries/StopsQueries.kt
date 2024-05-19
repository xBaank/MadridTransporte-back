package common.queries

import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import common.DB.calendarsCollection
import common.DB.stopsCollection
import common.DB.stopsInfoCollection
import common.DB.stopsOrderCollection
import common.exceptions.BusTrackerException
import common.models.*
import common.utils.timeZoneMadrid
import common.utils.toZoneOffset
import kotlinx.coroutines.flow.*
import java.time.*
import java.time.temporal.ChronoUnit


fun getAllStops(): Flow<Stop> = stopsCollection.find()
suspend fun getIdByStopCode(stopCode: String) =
    stopsInfoCollection.find(Filters.eq(StopInfo::idEstacion.name, stopCode))
        .firstOrNull()
        ?.codigoEmpresa
        ?.right()
        ?: BusTrackerException.NotFound().left()


suspend fun getStopNameById(codigoEmpresa: String) = either {
    val id = stopsInfoCollection.find(Filters.eq(StopInfo::codigoEmpresa.name, codigoEmpresa))
        .firstOrNull()
        ?.idEstacion
        ?: raise(BusTrackerException.NotFound())

    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, id))
        .firstOrNull()
        ?.stopName
        ?: raise(BusTrackerException.NotFound())
}

suspend fun getCoordinatesByStopCode(id: String) = stopsCollection.find(Filters.eq(Stop::fullStopCode.name, id))
    .firstOrNull()
    ?.let {
        Coordinates(
            it.stopLat,
            it.stopLon
        )
    }
    ?.right()
    ?: BusTrackerException.NotFound().left()

suspend fun getStopNameByStopCode(id: String) = either {
    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, id))
        .firstOrNull()
        ?.stopName
        ?: raise(BusTrackerException.NotFound("Stop with id $id not found"))
}

suspend fun checkStopExists(stopCode: String) = either {
    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, stopCode))
        .firstOrNull()
        ?: raise(BusTrackerException.NotFound("Stop with id $stopCode not found"))
}

suspend fun getStopTimesPlannedQuery(fullStopCode: String): Flow<StopOrderWithItineraries> {
    //We only want the services ids of this current moment
    val now = Instant.now().atZone(timeZoneMadrid.toZoneOffset())

    val nowUtc = Instant.now()
    val startOfDayUTC = nowUtc.truncatedTo(ChronoUnit.DAYS)
    val millisecondsSinceStartOfDay: Long = Duration.between(startOfDayUTC, nowUtc).toMillis()

    val dayFilter = when (now.dayOfWeek) {
        DayOfWeek.MONDAY -> Filters.eq(Calendar::monday.name, true)
        DayOfWeek.TUESDAY -> Filters.eq(Calendar::tuesday.name, true)
        DayOfWeek.WEDNESDAY -> Filters.eq(Calendar::wednesday.name, true)
        DayOfWeek.THURSDAY -> Filters.eq(Calendar::thursday.name, true)
        DayOfWeek.FRIDAY -> Filters.eq(Calendar::friday.name, true)
        DayOfWeek.SATURDAY -> Filters.eq(Calendar::saturday.name, true)
        DayOfWeek.SUNDAY -> Filters.eq(Calendar::sunday.name, true)
        else -> null
    }

    val filteredServicesIds = calendarsCollection.find(
        Filters.and(
            Filters.lte(Calendar::startDate.name, now.toInstant().toEpochMilli()),
            Filters.gte(Calendar::endDate.name, now.toInstant().toEpochMilli()),
            dayFilter
        )
    )
        .map { it.serviceId }
        .toList()

    val pipeline = listOf(
        Aggregates.match(
            Filters.and(
                Filters.eq(StopOrder::fullStopCode.name, fullStopCode),
                Filters.gte(StopOrder::departureTime.name, millisecondsSinceStartOfDay)
            )
        ),
        Aggregates.lookup(
            /* from = */ Itinerary::class.simpleName!!,
            /* localField = */ Itinerary::tripId.name,
            /* foreignField = */ StopOrder::tripId.name,
            /* as = */ StopOrderWithItineraries::itineraries.name
        )
    )

    return stopsOrderCollection.withDocumentClass<StopOrderWithItineraries>()
        .aggregate(pipeline)
        .filter { it.itineraries.any { it.serviceId in filteredServicesIds } }
        .map {
            it.copy(
                departureTime = it.departureTime + LocalDate.now(ZoneOffset.UTC)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli()
            )
        }
}