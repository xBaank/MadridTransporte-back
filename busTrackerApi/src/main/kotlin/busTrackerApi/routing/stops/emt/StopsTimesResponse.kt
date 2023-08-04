package busTrackerApi.routing.stops.emt

import arrow.core.continuations.either
import simpleJson.*

data class StopsTimesResponse(
    val arrives : List<Arrive>,
    val incidents : List<Incident>
)

data class Arrive(
    val line : String,
    val stop : String,
    val destination : String,
    val estimatedArrive : Int,
)

data class Incident(
    val title : String,
    val description : String,
    val cause : String,
    val effect : String,
    val url : String,
)

suspend fun parseToStopTimesResponse(json: JsonNode) = either {
    val arrives = json["data"][0]["Arrive"].asArray().bind()
    val incidents = json["data"][0]["Incident"]["ListaIncident"]["data"].asArray().bind()
    val arrivesMapped = arrives.map {
        Arrive(
            line = it["line"].asString().bind(),
            stop = it["stop"].asString().bind(),
            destination = it["destination"].asString().bind(),
            estimatedArrive = it["estimateArrive"].asInt().bind(),
        )
    }
    val incidentsMapped = incidents.map {
        Incident(
            title = it["title"].asString().bind(),
            description = it["description"].asString().bind(),
            cause = it["cause"].asString().bind(),
            effect = it["effect"].asString().bind(),
            url = it["moreinfo"]["@url"].asString().bind(),
        )
    }
    StopsTimesResponse(arrivesMapped, incidentsMapped)
}


fun buildJson(stopTimes : StopsTimesResponse) = jObject {
    "arrives" to jArray {
        stopTimes.arrives.forEach {
            +jObject {
                "line" += it.line
                "stop" += it.stop
                "destination" += it.destination
                "estimatedArrive" += it.estimatedArrive
            }
        }
    }
    "incidents" to jArray {
        stopTimes.incidents.forEach {
            +jObject {
                "title" += it.title
                "description" += it.description
                "cause" += it.cause
                "effect" += it.effect
                "url" += it.url
            }
        }
    }
}