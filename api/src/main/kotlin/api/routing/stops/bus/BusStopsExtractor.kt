package api.routing.stops.bus

import api.db.getItineraryByDestStop
import api.db.getItineraryByFullLineCode
import api.extensions.mapAsync
import api.extensions.toMiliseconds
import api.routing.stops.Arrive
import api.routing.stops.Coordinates
import api.routing.stops.StopTimes
import crtm.soap.StopTimesResponse

suspend fun extractBusStopTimes(
    response: StopTimesResponse?,
    coordinates: Coordinates,
    name: String?,
    shortStopCode: String?
): StopTimes {
    val arrives = response?.stopTimes?.times?.time?.mapAsync {
        Arrive(
            direction = it.direction,
            lineCode = it.line.codLine,
            line = it.line.shortDescription,
            destination = it.destination,
            codMode = it.line.codMode.toInt(),
            estimatedArrive = it.time.toMiliseconds(),
            itineraryCode = getItineraryByDestStop(
                it.line.codLine,
                it.direction,
                it.destinationStop.codStop
            )?.itineraryCode ?: getItineraryByFullLineCode(
                it.line.codLine,
                it.direction
            )?.itineraryCode
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