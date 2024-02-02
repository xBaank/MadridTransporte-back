package busTrackerApi.routing.lines.emt

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.routing.stops.emt.getEmtStopTimesResponse

suspend fun getLocationsResponse(stopCode: String, lineCode: String) = either {
    val stopTimes = getEmtStopTimesResponse(stopCode).bind() ?: shift<Nothing>(InternalServerError())
    extractEMTLineLocations(stopTimes, lineCode).bind()
}