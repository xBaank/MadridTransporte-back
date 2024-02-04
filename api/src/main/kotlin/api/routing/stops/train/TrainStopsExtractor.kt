package api.routing.stops.train

import api.routing.stops.Arrive
import api.routing.stops.Coordinates
import api.routing.stops.StopTimes
import api.routing.stops.trainRouted.trainCodMode
import api.utils.timeZoneMadrid
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.time.*
import java.time.format.DateTimeFormatter

fun extractTrainStopTimes(
    html: Document,
    coordinates: Coordinates,
    stopName: String,
    stopCode: String
): StopTimes {
    val table = html.selectXpath("/html/body/div/div[1]/table/tbody").firstOrNull()

    val arrives = table?.children()?.drop(1)?.mapNotNull {
        if (it.children().size < 5) return@mapNotNull null
        val salida = (it.child(0).firstElementChild()?.firstChild() as TextNode?)?.wholeText ?: ""
        val destino = (it.child(1).firstElementChild()?.firstChild() as TextNode?)?.wholeText ?: ""
        val linea = (it.child(3).firstChild() as TextNode?)?.wholeText ?: ""
        val anden = getAndenOrNull(it.child(4))
        Arrive(
            line = linea,
            codMode = trainCodMode.toInt(),
            destination = destino,
            anden = anden,
            estimatedArrive = parseSalida(salida)
        )
    }

    return StopTimes(
        trainCodMode.toInt(),
        stopName,
        coordinates,
        arrives,
        listOf(),
        stopCode
    )
}

fun createTrainFailedTimes(name: String, coordinates: Coordinates, stopCode: String) = StopTimes(
    codMode = trainCodMode.toInt(),
    stopName = name,
    coordinates = coordinates,
    arrives = null,
    incidents = emptyList(),
    simpleStopCode = stopCode
)

private fun getAndenOrNull(node: Element) =
    (node.firstElementChild()?.firstChild() as TextNode?)?.wholeText?.trim()?.toIntOrNull()
        ?: (node.firstChild() as TextNode?)?.wholeText?.trim()?.toIntOrNull()


private fun parseSalida(input: String): Long {
    val formattedInput = input.trim()

    if (formattedInput.isBlank()) {
        return LocalDateTime.now(Clock.systemUTC()).toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    if (formattedInput.contains("min")) {
        val minutes = formattedInput.removeSuffix("min").trim().toLong()
        val time = LocalDateTime.now(Clock.systemUTC()).plusMinutes(minutes).plusMinutes(1).minusSeconds(1)
        return time.toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    val nowMadridTime = ZonedDateTime.now(timeZoneMadrid.toZoneId())
    val formatted = LocalTime.parse(formattedInput, DateTimeFormatter.ofPattern("HH:mm"))
    var madridDateTime = ZonedDateTime.of(
        ZonedDateTime.now(timeZoneMadrid.toZoneId()).toLocalDate(),
        formatted,
        timeZoneMadrid.toZoneId()
    )

    if (madridDateTime < nowMadridTime) madridDateTime = madridDateTime.plusDays(1)

    return madridDateTime.toInstant().toEpochMilli()
}