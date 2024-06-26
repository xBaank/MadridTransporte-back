package api.routing.stops.train

import api.routing.stops.bus.getCRTMStopTimes
import api.routing.stops.train.cano.canoHttpClient
import arrow.core.raise.either
import common.exceptions.BusTrackerException.InternalServerError
import common.exceptions.BusTrackerException.NotFound
import common.extensions.awaitWrap
import common.extensions.bindJson
import common.extensions.post
import common.queries.getCoordinatesByStopCode
import common.queries.getIdByStopCode
import common.queries.getStopNameById
import common.utils.getStopCodeFromFullStopCode
import common.utils.trainCodMode
import simpleJson.deserialized
import simpleJson.jObject

suspend fun getTrainTimes(fullStopCode: String) = either {
    getCanoTrainTimes(fullStopCode).getOrNull() ?: getCRTMStopTimes(fullStopCode).bind()
        .let { times -> times.copy(arrives = times.arrives?.filter { it.codMode == trainCodMode.toInt() }) }
}

suspend fun getCanoTrainTimes(fullStopCode: String) = either {
    val stopInfoStationCode = getIdByStopCode(fullStopCode).bind()
    val stopName = getStopNameById(stopInfoStationCode).bind()
    val coordinates = getCoordinatesByStopCode(fullStopCode).bind()


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
    ).awaitWrap().bind()


    response.use {
        if (it.code == 404) raise(NotFound())
        if (!it.isSuccessful) raise(InternalServerError())

        val json = it.body?.string()?.deserialized()?.bindJson() ?: raise(InternalServerError())

        return@either extractTrainStopTimes(
            json,
            coordinates,
            stopName,
            getStopCodeFromFullStopCode(fullStopCode)
        ).bind()
    }
}