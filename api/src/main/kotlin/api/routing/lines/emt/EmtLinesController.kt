package api.routing.lines.emt

import api.exceptions.BusTrackerException.InternalServerError
import api.routing.stops.emt.getEmtStopTimesResponse
import arrow.core.continuations.either

suspend fun getLocationsResponse(stopCode: String, lineCode: String) = either {
    val stopTimes = getEmtStopTimesResponse(stopCode).bind() ?: shift<Nothing>(InternalServerError())
    extractEMTLineLocations(stopTimes, lineCode).bind()
}