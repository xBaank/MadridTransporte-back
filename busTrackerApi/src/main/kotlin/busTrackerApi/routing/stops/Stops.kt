package busTrackerApi.routing.stops

import crtm.auth
import crtm.defaultClient
import crtm.soap.ShortStopTimesRequest
import crtm.soap.ShortStopTimesResponse
import crtm.soap.ShortTime
import simpleJson.jObject

fun getStopTimes(stopCode : String, codMode : String?): ShortStopTimesResponse? {
    val request = ShortStopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.auth()
    }
    if(codMode != null) request.codMode = codMode
    return defaultClient.getShortStopTimes(request)
}

fun buildStopTimesJson(time: ShortTime) = jObject {
    "lineCode" += time.codLine
    "destination" += time.destination
    "time" += time.time.toGregorianCalendar().time.toInstant().toEpochMilli()
}