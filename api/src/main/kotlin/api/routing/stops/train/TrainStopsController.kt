package api.routing.stops.train

import api.config.httpClient
import api.db.getCoordinatesByStopCode
import api.db.getIdByStopCode
import api.db.getStopNameById
import api.exceptions.BusTrackerException.InternalServerError
import api.exceptions.BusTrackerException.NotFound
import api.extensions.post
import arrow.core.continuations.either
import crtm.utils.getStopCodeFromFullStopCode
import org.jsoup.Jsoup
import ru.gildor.coroutines.okhttp.await

suspend fun getTrainStopTimes(fullStopCode: String) = either {
    val stopInfoStationCode = getIdByStopCode(fullStopCode).bind()
    val stopName = getStopNameById(stopInfoStationCode).bind()
    val coordinates = getCoordinatesByStopCode(fullStopCode).bind()
    var tries = 3
    do {
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
            if (!it.isSuccessful) {
                tries--
                return@use
            }

            val html = it.body?.string()?.let(Jsoup::parse) ?: shift<Nothing>(InternalServerError())

            val result = extractTrainStopTimes(html, coordinates, stopName, getStopCodeFromFullStopCode(fullStopCode))

            if (result.arrives != null && result.arrives.isEmpty()) {
                tries--
                return@use
            }

            return@either result
        }
    } while (tries > 0)

    createTrainFailedTimes(
        name = stopName,
        coordinates = coordinates,
        stopCode = getStopCodeFromFullStopCode(fullStopCode)
    )
}
