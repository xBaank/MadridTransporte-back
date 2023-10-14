package busTrackerApi.db

data class Stop(
    val stopCode: String,
    val stopName: String,
    val stopLat: Double,
    val stopLon: Double,
    val codMode: Int,
    val fullStopCode: String
)