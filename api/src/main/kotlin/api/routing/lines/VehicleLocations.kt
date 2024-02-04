package api.routing.lines

import crtm.soap.VehicleLocation

data class VehicleLocations(
    val locations: List<VehicleLocation>,
    val codMode: Int,
    val lineCode: String
)