package busTrackerApi.routing.stops.train

import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.train.cano.canoHttpClient
import ru.gildor.coroutines.okhttp.await
import simpleJson.jObject

suspend fun test() {
    val call =
        canoHttpClient.post(
            "https://circulacion.api.adif.es/portroyalmanager/secure/circulationpaths/departures/traffictype/",
            jObject {
                "commercialService" += "YES"
                "commercialStopType" += "YES"
                "page" += jObject {
                    "pageNumber" += 0
                }
                "stationCode" += "37002"
                "trafficType" += "CERCANIAS"
            },
            contentType = null,
            headers = mapOf(
                "User-Key" to "f4ce9fbfa9d721e39b8984805901b5df",
                "Host" to "circulacion.api.adif.es",
                "User-Agent" to "okhttp/4.10.0",
                "Connection" to "Close"
            )
        )

    val result = call.await()
    assert(result.isSuccessful)
}