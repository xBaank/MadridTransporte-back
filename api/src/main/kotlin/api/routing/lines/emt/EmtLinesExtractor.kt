package api.routing.lines.emt

import api.db.getRoute
import api.extensions.bindJson
import api.extensions.toDirection
import api.routing.stops.emt.emtCodMode
import arrow.core.continuations.either
import crtm.soap.Coordinates
import crtm.soap.Line
import crtm.soap.VehicleLocation
import crtm.utils.createLineCode
import simpleJson.*

suspend fun extractEMTLineLocations(json: JsonNode, lineCode: String) = either {
    val arrives = json["data"][0]["Arrive"].asArray().bindJson()

    val lineLocations = arrives.filter { it["line"].asString().bindJson() == lineCode }
        .filter { it["DistanceBus"].asInt().bindJson() >= 0 }
        .filter { it["estimateArrive"].asInt().bindJson() != 999999 }

    val linesInfo = json["data"][0]["StopInfo"][0]["lines"].asArray().bindJson()

    lineLocations.map { location ->
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
                    codLine = getRoute(line, emtCodMode).getOrNull()?.fullLineCode ?: createLineCode(emtCodMode, line)
                    shortDescription = line
                }
            direction =
                linesInfo.first { it["label"].asString().bindJson() == lineCode }["to"].asString().bindJson()
                    .toDirection()
            service = ""
        }
    }
}