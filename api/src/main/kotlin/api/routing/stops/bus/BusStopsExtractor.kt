package api.routing.stops.bus

import api.extensions.toMiliseconds
import api.routing.stops.Arrive
import api.routing.stops.StopTimes
import common.utils.busCodMode
import crtm.soap.StopTimesResponse

fun extractCRTMStopTimes(
    response: StopTimesResponse?,
    coordinates: common.models.Coordinates,
    name: String?,
    shortStopCode: String?,
): StopTimes {
    val arrives = response?.stopTimes?.times?.time?.map {
        Arrive(
            direction = it.direction,
            lineCode = it.line.codLine,
            line = it.line.shortDescription,
            destination = it.destination,
            codMode = it.line.codMode.toInt(),
            estimatedArrive = it.time.toMiliseconds()
        )
    }

    return StopTimes(
        busCodMode.toInt(),
        name ?: response?.stopTimes?.stop?.name ?: "",
        coordinates,
        arrives?.sortedBy { it.line.toIntOrNull() },
        emptyList(),
        shortStopCode ?: response?.stopTimes?.stop?.shortCodStop ?: "",
    )
}