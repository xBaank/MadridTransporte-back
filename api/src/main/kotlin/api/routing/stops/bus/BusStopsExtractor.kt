package api.routing.stops.bus

import api.extensions.toMiliseconds
import api.routing.stops.Arrive
import api.routing.stops.StopTimes
import arrow.core.getOrElse
import arrow.core.raise.either
import common.extensions.bindJson
import common.models.Coordinates
import common.queries.getRoute
import common.utils.busCodMode
import common.utils.timeZoneMadrid
import common.utils.toZoneOffset
import crtm.soap.StopTimesResponse
import simpleJson.JsonNode
import simpleJson.asArray
import simpleJson.asString
import simpleJson.get
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun extractCRTMStopTimes(
    response: StopTimesResponse?,
    coordinates: Coordinates,
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

suspend fun extractAndMergeAvanzaBuses(
    response: JsonNode,
    times: StopTimes,
) = either {
    val arrivesNode = response["data"]["traficos"].asArray().bindJson()
    val arrives = arrivesNode.map {
        val hour = LocalTime.parse(it["llegada"].asString().bindJson(), DateTimeFormatter.ofPattern("HH:mm:ss"))
        val localDate = LocalDate.now(timeZoneMadrid.toZoneId())
        val madridTime = LocalDateTime.of(localDate, hour)
        val line = it["coLinea"].asString().bindJson()
        val arrive = Arrive(
            line = line,
            lineCode = getRoute(line, busCodMode).getOrNull()?.fullLineCode,
            destination = it["dsDestino"].asString().bindJson(),
            codMode = busCodMode.toInt(),
            estimatedArrive = madridTime.toInstant(timeZoneMadrid.toZoneOffset()).toEpochMilli()
        )

        val timesArrives = times.arrives?.filter { it.line == arrive.line } ?: listOf()

        timesArrives.ifEmpty { listOf(arrive) }
    }.flatten()

    times.copy(arrives = ((arrives + (times.arrives ?: listOf()))))
}.getOrElse { times }