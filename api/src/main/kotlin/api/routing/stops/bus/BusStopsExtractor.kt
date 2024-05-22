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
import common.utils.urbanCodMode
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
        arrives,
        emptyList(),
        shortStopCode ?: response?.stopTimes?.stop?.shortCodStop ?: "",
    )
}

suspend fun mergeAvanzaBuses(
    response: JsonNode,
    times: StopTimes,
) = either {
    val arrivesNode = response["data"]["traficos"].asArray().bindJson()
    val arrives = arrivesNode.map {
        val hour = LocalTime.parse(it["llegada"].asString().bindJson(), DateTimeFormatter.ofPattern("HH:mm:ss"))
        val localDate = LocalDate.now(timeZoneMadrid.toZoneId())
        val madridTime = LocalDateTime.of(localDate, hour)
        val line = it["coLineaWeb"].asString().bindJson()
        val route = getRoute(line, listOf(busCodMode, urbanCodMode)).getOrNull()
        val arrive = Arrive(
            line = line,
            lineCode = route?.fullLineCode,
            destination = "(Avanza) " + it["dsDestino"].asString().bindJson(),
            codMode = route?.codMode?.toInt() ?: busCodMode.toInt(),
            estimatedArrive = madridTime.toInstant(timeZoneMadrid.toZoneOffset()).toEpochMilli()
        )
        arrive
    }

    if (times.arrives?.map(Arrive::line)?.intersect(arrives.map(Arrive::line).toSet())?.isEmpty() == true) {
        times.copy(arrives = arrives + times.arrives)
    } else {
        times
    }

}.getOrElse { times }