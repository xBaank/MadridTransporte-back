package api.db

import api.config.stopsCollection
import api.config.stopsInfoCollection
import api.db.models.Stop
import api.db.models.StopInfo
import api.exceptions.BusTrackerException
import api.routing.stops.Coordinates
import arrow.core.Either
import arrow.core.continuations.either
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull

fun getAllStops() = stopsCollection.find()
suspend fun getIdByStopCode(stopCode: String) = either {
    stopsInfoCollection.find(Filters.eq(StopInfo::idEstacion.name, stopCode))
        .firstOrNull()
        ?.codigoEmpresa
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with stopCode $stopCode not found"))
}

suspend fun getStopNameById(codigoEmpresa: String) = either {
    val id = stopsInfoCollection.find(Filters.eq(StopInfo::codigoEmpresa.name, codigoEmpresa))
        .firstOrNull()
        ?.idEstacion
        ?: shift<Nothing>(BusTrackerException.NotFound("Stop with id $codigoEmpresa not found"))

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