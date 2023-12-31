package busTrackerApi.routing.lines.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import crtm.soap.Coordinates
import crtm.soap.Line
import crtm.soap.VehicleLocation
import simpleJson.JsonNode

suspend fun parseEMTToLocation(json: JsonNode): Either<BusTrackerException, List<VehicleLocation>> = either {
    listOf(VehicleLocation().apply {
        codVehicle = ""
        coordinates = Coordinates().apply { latitude = 1.0; longitude = 1.0 }
        line = Line().apply { codLine = "" }
        direction = 1
        service = ""
    })
}