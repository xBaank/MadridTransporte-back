package busTrackerApi.db.models

data class Itinerary(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
    val tripId: String
)

data class ItineraryWithStops(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
    val stops: List<StopOrder>,
    val tripId: String?
)

data class StopOrder(
    val fullStopCode: String,
    val tripId: String?,
    val order: Int
)