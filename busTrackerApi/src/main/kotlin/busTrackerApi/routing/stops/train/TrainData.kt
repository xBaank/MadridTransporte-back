package busTrackerApi.routing.stops.train

import java.time.LocalTime

data class SimpleTrainStopData(
    val line: String,
    val stationName: String,
    val lastStop: String,
)

data class TrainStopData(
    val line: String,
    val stationName: String,
    val stationCode: String,
    val lastStop: String,
    val lastStopCode: String,
)

data class SimpleTrainLineData(
    val origin : String,
    val line: String,
    val destination: String,
    val hour: LocalTime,
    val hourFormatted: String = hour.format(hourFormatter)
)

data class TrainLineData(
    val origin: String,
    val originId: String,
    val line: String,
    val destination: String,
    val destinationId: String,
    val hour: LocalTime,
    val hourFormatted: String = hour.format(hourFormatter)
)

data class TrainTimes(
    val origin: String,
    val times: List<TrainTime>
)

data class TrainTime(
    val line : String,
    val destination: String,
    val trainCode: String,
    val salida: String,
    val llegada: String,
    val duracion: String,
)