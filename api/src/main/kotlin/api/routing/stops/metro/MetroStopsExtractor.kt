package api.routing.stops.metro

import api.db.getRoute
import api.extensions.toZoneOffset
import api.routing.stops.Arrive
import api.routing.stops.Coordinates
import api.routing.stops.StopTimes
import api.utils.timeZoneMadrid
import arrow.core.continuations.either
import crtm.utils.createLineCode
import simpleJson.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun extractMetroStopTimes(
    json: JsonNode?,
    codMode: String,
    coordinates: Coordinates,
    name: String,
    simpleStopCode: String
) =
    either {
        val arrives = json?.asArray()?.bind()

        val arrivesMapped = arrives?.flatMap { arrive ->
            val proximo = arrive["proximo"].asLong().getOrNull()
            val siguiente = arrive["siguiente"].asLong().getOrNull()

            val fecharHoraEmision = arrive["fechaHoraEmisionPrevision"].asString().bind()
            val fecharHoraEmisionParsed = LocalDateTime.parse(fecharHoraEmision, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val now = LocalDateTime.now(timeZoneMadrid.toZoneId())

            val diffInMinutes = fecharHoraEmisionParsed.until(now, java.time.temporal.ChronoUnit.MINUTES)
            val proximoDiff = proximo?.let { it - diffInMinutes }?.takeIf { it >= 0 }
            val siguienteDiff = siguiente?.let { it - diffInMinutes }?.takeIf { it >= 0 }

            val proximoEstimatedArrive = proximoDiff
                ?.let { now.plusMinutes(it + 1).minusSeconds(1) }
                ?.toInstant(timeZoneMadrid.toZoneOffset())?.toEpochMilli()

            val siguienteEstimatedArrive = siguienteDiff
                ?.let { now.plusMinutes(it + 1).minusSeconds(1) }
                ?.toInstant(timeZoneMadrid.toZoneOffset())?.toEpochMilli()

            val line = arrive["linea"].asNumber().bind().toString()
            val lineCode = getRoute(line, metroCodMode).getOrNull()?.fullLineCode ?: createLineCode(metroCodMode, line)

            val first = Arrive(
                line = line,
                lineCode = lineCode,
                destination = arrive["sentido"].asString().bind(),
                anden = arrive["anden"].asInt().getOrNull(),
                codMode = metroCodMode.toInt(),
                estimatedArrive = proximoEstimatedArrive ?: -1
            )

            val second = Arrive(
                line = line,
                lineCode = lineCode,
                destination = arrive["sentido"].asString().bind(),
                anden = arrive["anden"].asInt().getOrNull(),
                codMode = metroCodMode.toInt(),
                estimatedArrive = siguienteEstimatedArrive ?: -1
            )

            listOf(first, second).filter { it.estimatedArrive >= 0 }
        }

        StopTimes(codMode.toInt(), name, coordinates, arrivesMapped, emptyList(), simpleStopCode)
    }