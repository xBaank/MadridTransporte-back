package busTrackerApi.routing.stops.train

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