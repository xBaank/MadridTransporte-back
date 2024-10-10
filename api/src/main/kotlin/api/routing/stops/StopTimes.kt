package api.routing.stops

import common.models.Coordinates

data class StopTimes(
    val codMode: Int,
    val stopName: String,
    val coordinates: Coordinates,
    val arrives: List<Arrive>?,
    val incidents: List<Incident>,
    val simpleStopCode: String,
    val stopCode: String = "${codMode}_${simpleStopCode}",
)

data class Arrive(
    val line: String,
    val lineCode: String? = null,
    val direction: Int? = null,
    val codMode: Int,
    val anden: Int? = null,
    val destination: String,
    val estimatedArrive: Long,
)

data class Incident(
    val title: String,
    val description: String,
    val from: String,
    val to: String,
    val cause: String,
    val effect: String,
    val url: String,
)

