package busTrackerApi.db

data class Itinerary(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
    val stops: List<StopOrder>
)

data class StopOrder(
    val fullStopCode: String,
    val order: Int
)