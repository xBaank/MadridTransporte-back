package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Coordinates
import busTrackerApi.routing.stops.StopTimes
import simpleJson.*
import java.time.LocalDateTime
import java.time.ZoneOffset

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

            if ((proximo == null) && (siguiente == null)) return@flatMap emptyList()

            val proximoEstimatedArrive = proximo
                ?.let { LocalDateTime.now(ZoneOffset.UTC).plusMinutes(it) }
                ?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

            val siguienteEstimatedArrive = siguiente
                ?.let { LocalDateTime.now(ZoneOffset.UTC).plusMinutes(it) }
                ?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

            val first = Arrive(
                line = arrive["linea"].asNumber().bind().toString(),
                destination = arrive["sentido"].asString().bind(),
                anden = arrive["anden"].asInt().getOrNull(),
                codMode = metroCodMode.toInt(),
                estimatedArrive = proximoEstimatedArrive ?: -1
            )

            val second = first.copy(estimatedArrive = siguienteEstimatedArrive ?: -1)

            listOf(first, second).filter { it.estimatedArrive != -1L }
        }

        StopTimes(codMode.toInt(), name, coordinates, arrivesMapped, emptyList(), simpleStopCode)
    }