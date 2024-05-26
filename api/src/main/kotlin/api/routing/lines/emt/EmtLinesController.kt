package api.routing.lines.emt

import api.routing.stops.emt.getEmtStopTimesResponse
import arrow.core.raise.either
import common.exceptions.BusTrackerException


suspend fun getLocationsResponse(stopCode: String, lineCode: String) = either {
    val stopTimes = getEmtStopTimesResponse(stopCode).bind() ?: raise(BusTrackerException.InternalServerError())
    parseEMTToLocation(stopTimes, lineCode).bind()
}