package api.routing.stops.train

import api.config.httpClient
import api.db.getCoordinatesByStopCode
import api.db.getIdByStopCode
import api.db.getStopNameById
import api.exceptions.BusTrackerException.InternalServerError
import api.exceptions.BusTrackerException.NotFound
import api.extensions.bindJson
import api.extensions.post
import api.routing.stops.train.cano.canoHttpClient
import arrow.core.continuations.either
import crtm.utils.getStopCodeFromFullStopCode
import org.jsoup.Jsoup
import ru.gildor.coroutines.okhttp.await
import simpleJson.deserialized
import simpleJson.jObject

suspend fun getHtmlTrainStopTimes(fullStopCode: String) = either {
    val stopInfoStationCode = getIdByStopCode(fullStopCode).bind()
    val stopName = getStopNameById(stopInfoStationCode).bind()
    val coordinates = getCoordinatesByStopCode(fullStopCode).bind()
    val request =
        "station=${stopInfoStationCode}&dest=&previous=1&showCercanias=true&showOtros=false&iframe=false&isNative=true"
    
    val response = httpClient.post(
        "https://elcanoweb.adif.es/departures/reload",
        request,
        contentType = "application/x-www-form-urlencoded; charset=utf-8",
        headers = mapOf(
            "Authorization" to "Basic ZGVpbW9zOmRlaW1vc3R0",
            "Host" to "elcanoweb.adif.es",
            "User-Agent" to "okhttp/4.12.0"
        )
    ).await()

    response.use {
        if (it.code == 404) shift<Nothing>(NotFound())
        if (!it.isSuccessful) return@use

        val html = it.body?.string()?.let(Jsoup::parse) ?: shift<Nothing>(InternalServerError())

        val result = extractTrainStopTimes(html, coordinates, stopName, getStopCodeFromFullStopCode(fullStopCode))

        if (result.arrives != null && result.arrives.isEmpty()) return@use

        return@either result
    }

    createTrainFailedTimes(
        name = stopName,
        coordinates = coordinates,
        stopCode = getStopCodeFromFullStopCode(fullStopCode)
    )
}

suspend fun getTrainTimes(fullStopCode: String) = either {
    getCanoTrainTimes(fullStopCode).getOrNull() ?: getHtmlTrainStopTimes(fullStopCode).bind()
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
    ).await()


    response.use {
        if (it.code == 404) shift<Nothing>(NotFound())
        if (!it.isSuccessful) shift<Nothing>(InternalServerError())

        val json = it.body?.string()?.deserialized()?.bindJson() ?: shift<Nothing>(InternalServerError())

        return@either extractTrainStopTimes(
            json,
            coordinates,
            stopName,
            getStopCodeFromFullStopCode(fullStopCode)
        ).bind()
    }
}