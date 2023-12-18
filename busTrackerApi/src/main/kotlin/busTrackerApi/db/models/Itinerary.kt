package busTrackerApi.db.models

data class Itinerary(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
    val tripId: String
)

data class StopOrder(
    val fullStopCode: String,
    val order: Int
)