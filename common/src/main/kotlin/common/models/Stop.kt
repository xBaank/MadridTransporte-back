package common.models

data class Stop(
    val stopCode: String,
    val stopName: String,
    val stopLat: Double,
    val stopLon: Double,
    val codMode: Int,
    val fullStopCode: String,
    val wheelchair: Int,
    val zone: String,
)