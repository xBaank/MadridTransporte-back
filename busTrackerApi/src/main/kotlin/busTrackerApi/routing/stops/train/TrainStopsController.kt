package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.db.getCoordinatesByStopCode
import busTrackerApi.db.getIdByStopCode
import busTrackerApi.db.getStopNameById
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.post
import crtm.utils.getStopCodeFromFullStopCode
import org.jsoup.Jsoup
import ru.gildor.coroutines.okhttp.await

suspend fun getTrainTimes(fullStopCode: String) = either {
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

            val result = parseTrainToStopTimes(html, coordinates, stopName, getStopCodeFromFullStopCode(fullStopCode))

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
