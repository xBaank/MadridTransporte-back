package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import busTrackerApi.extensions.toZoneOffset
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Coordinates
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.utils.timeZoneMadrid
import simpleJson.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun parseMetroToStopTimes(
    json: JsonNode,
    codMode: String,
    coordinates: Coordinates,
    name: String,
    simpleStopCode: String
) =
    either {
        val arrives = json.asArray().bind()
        if (arrives.isEmpty()) return@either StopTimes(
            codMode.toInt(),
            name,
            coordinates,
            emptyList(),
            emptyList(),
            simpleStopCode
        )

        val arrivesMapped = arrives.flatMap { arrive ->
            val proximo = arrive["proximo"].asLong().getOrNull()
            val siguiente = arrive["siguiente"].asLong().getOrNull()

            val fecharHoraEmision = arrive["fechaHoraEmisionPrevision"].asString().bind()
            val fecharHoraEmisionParsed = LocalDateTime.parse(fecharHoraEmision, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val now = LocalDateTime.now(timeZoneMadrid.toZoneId())

            val diffInMinutes = fecharHoraEmisionParsed.until(now, java.time.temporal.ChronoUnit.MINUTES)
            val proximoDiff = proximo?.let { it - diffInMinutes }?.takeIf { it >= 0 }
            val siguienteDiff = siguiente?.let { it - diffInMinutes }?.takeIf { it >= 0 }

            val proximoEstimatedArrive = proximoDiff
                ?.let { now.plusMinutes(it) }
                ?.toInstant(timeZoneMadrid.toZoneOffset())?.toEpochMilli()

            val siguienteEstimatedArrive = siguienteDiff
                ?.let { now.plusMinutes(it) }
                ?.toInstant(timeZoneMadrid.toZoneOffset())?.toEpochMilli()

            val first = Arrive(
                line = arrive["linea"].asNumber().bind().toString(),
                destination = arrive["sentido"].asString().bind(),
                anden = arrive["anden"].asInt().getOrNull(),
                codMode = metroCodMode.toInt(),
                estimatedArrive = proximoEstimatedArrive ?: -1
            )

            val second = Arrive(
                line = arrive["linea"].asNumber().bind().toString(),
                destination = arrive["sentido"].asString().bind(),
                anden = arrive["anden"].asInt().getOrNull(),
                codMode = metroCodMode.toInt(),
                estimatedArrive = siguienteEstimatedArrive ?: -1
            )

            listOf(first, second).filter { it.estimatedArrive >= 0 }
        }

        StopTimes(codMode.toInt(), name, coordinates, arrivesMapped, emptyList(), simpleStopCode)
    }