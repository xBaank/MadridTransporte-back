package api.utils

import api.routing.stops.bus.busCodMode
import api.routing.stops.bus.urbanCodMode
import api.routing.stops.emt.emtCodMode

fun fixColor(codMode: String, kml: String) = when (codMode) {
    busCodMode -> kml.replace("<color>[0-9a-fA-F]{8}</color>".toRegex(), "<color>ff00ff00</color>")
        .replace("<width>\\d+</width>".toRegex(), "<width>4</width>")

    urbanCodMode -> kml.replace("<color>[0-9a-fA-F]{8}</color>".toRegex(), "<color>ff0000ff</color>")
        .replace("<width>\\d+</width>".toRegex(), "<width>4</width>")

    emtCodMode -> kml.replace("<color>[0-9a-fA-F]{8}</color>".toRegex(), "<color>ffff6021</color>")
        .replace("<width>\\d+</width>".toRegex(), "<width>4</width>")

    else -> kml
}