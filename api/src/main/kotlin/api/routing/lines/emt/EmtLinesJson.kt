package api.routing.lines.emt

import api.extensions.toDirection
import api.routing.lines.VehicleLocations
import arrow.core.raise.either
import common.extensions.bindJson
import common.utils.createLineCode
import common.utils.emtCodMode
import crtm.soap.Coordinates
import crtm.soap.Line
import crtm.soap.VehicleLocation
import simpleJson.*

fun parseEMTToLocation(json: JsonNode, lineCode: String) = either {
    val arrives = json["data"][0]["Arrive"].asArray().bindJson()

    val lineLocations = arrives.filter { it["line"].asString().bindJson() == lineCode }
        .filter { it["DistanceBus"].asInt().bindJson() >= 0 }
        .filter { it["estimateArrive"].asInt().bindJson() != 999999 }

    val linesInfo = json["data"][0]["StopInfo"][0]["lines"].asArray().bindJson()

    val locations = lineLocations.map { location ->
        val coordinatesJson = location["geometry"]["coordinates"].asArray().bindJson()
        VehicleLocation().apply {
            codVehicle = ""
            coordinates = Coordinates().apply {
                latitude = coordinatesJson[1].asDouble().bindJson()
                longitude = coordinatesJson[0].asDouble().bindJson()
            }
            line =
                Line().apply {
                    val line = location["line"].asString().bindJson()
                    codLine = createLineCode(emtCodMode, line)
                    shortDescription = line
                }
            direction =
                linesInfo.first { it["label"].asString().bindJson() == lineCode }["to"].asString().bindJson()
                    .toDirection()
            service = ""
        }
    }

    VehicleLocations(locations, emtCodMode.toInt(), lineCode)
}