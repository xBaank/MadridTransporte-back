package api.db.models

data class Itinerary(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
    val tripId: String,
    val serviceId: String,
    val tripName: String,
)

data class ItineraryWithStops(
    val itineraryCode: String,
    val fullLineCode: String,
    val direction: Int,
    val stops: List<StopOrder>,
    val tripId: String?,
)

data class StopOrder(
    val fullStopCode: String,
    val tripId: String,
    val order: Int,
    val departureTime: Long,
)

data class StopOrderWithItineraries(
    val fullStopCode: String,
    val tripId: String,
    val itineraries: List<Itinerary>,
    val order: Int,
    val departureTime: Long,
)