package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.StopTimes
import simpleJson.*
import java.time.LocalDateTime
import java.time.ZoneOffset

suspend fun parseMetroToStopTimes(json: JsonNode) = either {
    val arrives = json.asArray().bind()

    val arrivesMapped = arrives.flatMap {

        val proximoEstimatedArrive = it["proximo"]
            .asLong()
            .map { LocalDateTime.now().plusMinutes(it) }
            .map { it.toEpochSecond(ZoneOffset.UTC) }
            .getOrNull()
        val siguienteEstimatedArrive = it["siguiente"]
            .asLong()
            .map { LocalDateTime.now().plusMinutes(it) }
            .map { it.toEpochSecond(ZoneOffset.UTC) }
            .getOrNull()

        val first = Arrive(
            line = it["linea"].asNumber().bind().toString(),
            stop = it["nombreest"].asString().bind(),
            destination = it["sentido"].asString().bind(),
            estimatedArrive = proximoEstimatedArrive,
        )

        val second =
            if (siguienteEstimatedArrive != null) first.copy(estimatedArrive = siguienteEstimatedArrive) else null

        listOfNotNull(first, second)
    }

    StopTimes(arrivesMapped, emptyList())
}