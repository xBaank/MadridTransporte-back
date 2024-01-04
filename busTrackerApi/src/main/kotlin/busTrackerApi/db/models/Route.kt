package busTrackerApi.db.models

data class Route(
    val fullLineCode: String,
    val simpleLineCode: String,
    val routeName: String,
    val codMode: String
)
