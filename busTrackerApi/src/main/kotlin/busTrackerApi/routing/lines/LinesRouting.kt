package busTrackerApi.routing.lines

import arrow.core.getOrElse
import crtm.utils.createLineCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.asArray
import simpleJson.asJson
import simpleJson.jArray
import simpleJson.serialize

fun Route.linesRouting() = route("/lines") {
    get("/{lineCode}/locations") {
        val lineCode = createLineCode("8", call.parameters["lineCode"]!!)

        val itineraries = getItineraries(lineCode)
        ?:  return@get call.respond(HttpStatusCode.NotFound)

        val locations = itineraries.itineraries.lineItinerary.map {
            getLocations(it, lineCode, "8")
        }

        val json = locations.mapNotNull {
            it?.vehiclesLocation?.vehicleLocation?.map(::buildVehicleLocationJson)
        }.flatten().asJson()

        call.respondText(json.serialize(), ContentType.Application.Json)
    }

    get("/{lineCode}/mode/{codMode}/stops") {
        val lineCode = createLineCode(call.parameters["codMode"]!!, call.parameters["lineCode"]!!)
        val codMode = call.parameters["codMode"]!!

        val stops = getStops(lineCode, codMode)
        ?:  return@get call.respond(HttpStatusCode.NotFound)

        val json = jArray {
            addAll(stops.stops.stop.map(::buildStopsJson).asJson())
        }

        call.respondText(json.serialize(), ContentType.Application.Json)
    }

    get("/{lineCode}/mode/{codMode}/itineraries") {
        val lineCode = createLineCode(call.parameters["codMode"]!!, call.parameters["lineCode"]!!)

        val itineraries = getItineraries(lineCode)
        ?:  return@get call.respond(HttpStatusCode.NotFound)

        val json = jArray {
            addAll(itineraries.itineraries.lineItinerary.map(::buildItinerariesJson).asJson())
        }

        call.respondText(json.serialize(), ContentType.Application.Json)
    }
}

