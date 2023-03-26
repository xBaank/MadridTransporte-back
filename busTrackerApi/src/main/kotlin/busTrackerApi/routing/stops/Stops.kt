package busTrackerApi.routing.stops

import crtm.auth
import crtm.defaultClient
import crtm.soap.StopTimesRequest
import crtm.soap.StopTimesResponse
import crtm.soap.Time
import simpleJson.jObject

fun getStopTimes(stopCode : String, codMode : String): StopTimesResponse? {
    val request = StopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 1
        stopTimesByIti = 3
        this.codMode = codMode
        authentication = defaultClient.auth()
    }
    return defaultClient.getStopTimes(request)
}

fun buildStopTimesJson(time: Time) = jObject {
    "lineCode" += time.line.codLine
    "destination" += time.destination
    "time" += time.time.toGregorianCalendar().time.toInstant().toEpochMilli()
}