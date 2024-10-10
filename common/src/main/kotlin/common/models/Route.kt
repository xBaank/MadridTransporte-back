package common.models

data class Route(
    val fullLineCode: String,
    val simpleLineCode: String,
    val routeName: String,
    val codMode: String,
)

data class RouteWithItineraries(
    val fullLineCode: String,
    val simpleLineCode: String,
    val routeName: String,
    val codMode: String,
    val itineraries: List<Itinerary>,
)
