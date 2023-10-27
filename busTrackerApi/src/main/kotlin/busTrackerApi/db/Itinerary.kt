package busTrackerApi.db

data class Itinerary(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
)