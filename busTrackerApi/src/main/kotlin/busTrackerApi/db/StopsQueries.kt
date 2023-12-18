package busTrackerApi.db

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.stopsCollection
import busTrackerApi.config.stopsInfoCollection
import busTrackerApi.db.models.Stop
import busTrackerApi.db.models.StopInfo
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.routing.stops.Coordinates
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull

suspend fun getIdByStopCode(stopCode: String) = either {
    stopsInfoCollection.find(Filters.eq(StopInfo::idEstacion.name, stopCode))
        .firstOrNull()
        ?.codigoEmpresa
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with stopCode $stopCode not found"))
}

suspend fun getStopCodeById(id: String) = either {
    stopsInfoCollection.find(Filters.eq(StopInfo::codigoEmpresa.name, id))
        .firstOrNull()
        ?.idEstacion
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $id not found"))
}

suspend fun getStopNameById(id: String) = either {
    val id = stopsInfoCollection.find(Filters.eq(StopInfo::codigoEmpresa.name, id))
        .firstOrNull()
        ?.idEstacion
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $id not found"))

    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, id))
        .firstOrNull()
        ?.stopName
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $id not found"))
}

suspend fun getCoordinatesByStopCode(id: String): Either<BusTrackerException, Coordinates> = either {
    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, id))
        .firstOrNull()
        ?.let {
            Coordinates(
                it.stopLat,
                it.stopLon
            )
        }
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $id not found"))
}

suspend fun getStopNameByStopCode(id: String) = either {
    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, id))
        .firstOrNull()
        ?.stopName
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $id not found"))
}

suspend fun checkStopExists(stopCode: String) = either {
    stopsCollection.find(Filters.eq(Stop::fullStopCode.name, stopCode))
        .firstOrNull()
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $stopCode not found"))
}