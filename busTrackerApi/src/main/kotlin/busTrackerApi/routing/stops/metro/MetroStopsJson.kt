package busTrackerApi.routing.stops.metro

import arrow.core.continuations.either
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.StopTimes
import simpleJson.*
import java.time.LocalDateTime
import java.time.ZoneOffset

suspend fun parseMetroToStopTimes(json: JsonNode, codMode: String) = either {
    val arrives = json.asArray().bind()
    val stopName = arrives[0]["nombreest"].asString().bind()

    val arrivesMapped = arrives.flatMap {
        val proximo = it["proximo"].asLong().getOrNull()
        val siguiente = it["siguiente"].asLong().getOrNull()

        if(proximo == null || proximo == 0L && siguiente == null || siguiente == 0L) return@flatMap emptyList()

        val proximoEstimatedArrive = proximo
            .let { LocalDateTime.now(ZoneOffset.UTC).plusMinutes(it) }
            .toInstant(ZoneOffset.UTC)?.toEpochMilli()

        val siguienteEstimatedArrive = siguiente
            ?.let { LocalDateTime.now(ZoneOffset.UTC).plusMinutes(it) }
            ?.toInstant(ZoneOffset.UTC)?.toEpochMilli()

        val first = if(proximoEstimatedArrive != null) Arrive(
            line = it["linea"].asNumber().bind().toString(),
            stop = it["nombreest"].asString().bind(),
            destination = it["sentido"].asString().bind(),
            estimatedArrive = proximoEstimatedArrive,
        ) else null

        val second =
            if (siguienteEstimatedArrive != null) first?.copy(estimatedArrive = siguienteEstimatedArrive) else null

        listOfNotNull(first, second)
    }

    StopTimes(codMode, stopName, arrivesMapped, emptyList())
}