package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.EnvVariables.timeoutSeconds
import busTrackerApi.db.getCoordinatesByStopCode
import busTrackerApi.db.getIdByStopCode
import busTrackerApi.db.getStopNameById
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.await
import busTrackerApi.extensions.bindJson
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.train.cano.canoHttpClient
import crtm.utils.getStopCodeFromFullStopCode
import simpleJson.deserialized
import simpleJson.jObject

suspend fun getTrainRoutedTimesResponse(fullStopCode: String) = either {
    val stopInfoStationCode = getIdByStopCode(fullStopCode).bind()
    val stopName = getStopNameById(stopInfoStationCode).bind()
    val coordinates = getCoordinatesByStopCode(fullStopCode).bind()

    var tries = 3
    do {
        val response = canoHttpClient.post(
            "https://circulacion.api.adif.es/portroyalmanager/secure/circulationpaths/departures/traffictype/",
            jObject {
                "commercialService" += "YES"
                "commercialStopType" += "YES"
                "page" += jObject {
                    "pageNumber" += 0
                }
                "stationCode" += stopInfoStationCode
                "trafficType" += "CERCANIAS"
            },
            contentType = null,
            headers = mapOf(
                "User-Key" to "f4ce9fbfa9d721e39b8984805901b5df",
                "Host" to "circulacion.api.adif.es",
                "User-Agent" to "okhttp/4.10.0",
                "Connection" to "Close"
            )
        ).await(timeoutSeconds)

        if (response == null) {
            tries--
            continue
        }


        response.use {
            if (it.code == 404) shift<Nothing>(NotFound())
            if (!it.isSuccessful) {
                tries--
                return@use
            }

            val json = it.body?.string()?.deserialized()?.bindJson() ?: shift<Nothing>(InternalServerError())

            parseTrainToStopTimes(json, coordinates, stopName, getStopCodeFromFullStopCode(fullStopCode)).bind()
        }
    } while (tries > 0)

    createTrainFailedTimes(
        name = stopName,
        coordinates = coordinates,
        stopCode = getStopCodeFromFullStopCode(fullStopCode)
    )
}
