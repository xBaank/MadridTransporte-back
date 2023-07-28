package busTrackerApi.routing.stops.train

data class SimpleTrainData(
    val line: String,
    val stationName: String,
    val lastStop: String,
)

data class TrainData(
    val line: String,
    val stationName: String,
    val stationCode: String,
    val lastStop: String,
    val lastStopCode: String,
)