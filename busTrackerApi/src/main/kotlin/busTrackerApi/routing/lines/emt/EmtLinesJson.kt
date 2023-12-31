package busTrackerApi.routing.lines.emt

import arrow.core.continuations.either
import busTrackerApi.extensions.bindJson
import busTrackerApi.extensions.toDirection
import crtm.soap.Coordinates
import crtm.soap.Line
import crtm.soap.VehicleLocation
import simpleJson.*

suspend fun parseEMTToLocation(json: JsonNode, lineCode: String) = either {
    //Get last - 1
    val arrives = json["data"][0]["Arrive"].asArray().bindJson()

    val lineLocations = arrives.filter { it["line"].asString().bindJson() == lineCode }
        .filter { it["DistanceBus"].asInt().bindJson() >= 0 }
        .filter { it["estimateArrive"].asInt().bindJson() != 999999 }

    val linesInfo = json["data"][0]["StopInfo"][0]["lines"].asArray().bindJson()

    lineLocations.map {
        val coordinatesJson = it["geometry"]["coordinates"].asArray().bindJson()
        VehicleLocation().apply {
            codVehicle = ""
            coordinates = Coordinates().apply {
                latitude = coordinatesJson[1].asDouble().bindJson()
                longitude = coordinatesJson[0].asDouble().bindJson()
            }
            line = Line().apply { codLine = it["line"].asString().bindJson() }
            direction =
                linesInfo.first { it["label"].asString().bindJson() == lineCode }["to"].asString().bindJson()
                    .toDirection()
            service = ""
        }
    }
}