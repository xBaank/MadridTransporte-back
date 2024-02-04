package api.db.models

data class Shape(
    val itineraryId: String,
    val latitude: Double,
    val longitude: Double,
    val sequence: Int,
    val distance: Double
)