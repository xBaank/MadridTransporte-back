package busTrackerApi.routing.stops

data class StopTimes(
    val codMode: String,
    val stopName: String,
    val arrives : List<Arrive>,
    val incidents : List<Incident>
)

data class Arrive(
    val line : String,
    val stop : String,
    val destination : String,
    val estimatedArrive : Long,
)

data class Incident(
    val title : String,
    val description : String,
    val cause : String,
    val effect : String,
    val url : List<String>,
)


