package busTrackerApi.routing.bus.lines

import crtm.utils.getCodModeFromLineCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import simpleJson.asJson
import simpleJson.serialized

fun Route.linesRouting() = route("/lines") {
    get("/{lineCode}/locations") {
        val lineCode = call.parameters["lineCode"]!!
        val codMode = getCodModeFromLineCode(lineCode)


        val itineraries = getItineraries(lineCode)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val locations = itineraries.itineraries.lineItinerary.map {
            getLocations(it, lineCode, codMode)
        }

        val json = locations.mapNotNull {
            it?.vehiclesLocation?.vehicleLocation?.map(::buildVehicleLocationJson)
        }.flatten().asJson()

        call.respondText(json.serialized(), ContentType.Application.Json)
    }

    get("/{lineCode}/stops") {
        val lineCode = call.parameters["lineCode"]!!
        val codMode = getCodModeFromLineCode(lineCode)

        val stops = getStops(lineCode, codMode)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = stops.stops.stop.map(::buildStopsJson).asJson()

        call.respondText(json.serialized(), ContentType.Application.Json)
    }

    get("/{lineCode}/itineraries") {
        val lineCode = call.parameters["lineCode"]!!

        val itineraries = getItineraries(lineCode)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        val json = itineraries.itineraries.lineItinerary.map(::buildItinerariesJson).asJson()

        call.respondText(json.serialized(), ContentType.Application.Json)
    }
}

