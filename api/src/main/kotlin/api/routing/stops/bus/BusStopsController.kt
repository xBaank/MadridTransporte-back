package api.routing.stops.bus

import api.config.EnvVariables
import api.config.httpClient
import api.utils.auth
import api.utils.defaultClient
import api.utils.getSuspend
import api.utils.mapExceptionsF
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import common.exceptions.BusTrackerException.InternalServerError
import common.extensions.bindJson
import common.extensions.get
import common.queries.getCoordinatesByStopCode
import common.queries.getStopNameByStopCode
import common.utils.getStopCodeFromFullStopCode
import crtm.soap.StopTimesRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import ru.gildor.coroutines.okhttp.await
import simpleJson.asJson
import simpleJson.deserialized
import java.io.InterruptedIOException

private suspend fun getStopTimesResponse(stopCode: String) = Either.catch {
    val request = StopTimesRequest().apply {
        codStop = stopCode
        type = 1
        orderBy = 2
        stopTimesByIti = 3
        authentication = defaultClient.value().auth()
    }
    getSuspend(request, defaultClient.value()::getStopTimesAsync)
}.mapLeft(mapExceptionsF)

private suspend fun getAvanzaData(simpleStopCode: String) = either {
    val url = "https://apisvt.avanzagrupo.com/lineas/getTraficosParada?empresa=25&parada=$simpleStopCode"

    val response = try {
        httpClient.get(url).await()
    } catch (ex: InterruptedIOException) {
        raise(InternalServerError("Avanza timeout error"))
    }

    if (!response.isSuccessful) raise(InternalServerError("Avanza error"))
    response.body?.string()?.deserialized()?.bindJson() ?: raise(InternalServerError("Body is null"))
}.getOrElse { null.asJson() }

suspend fun getCRTMStopTimes(stopCode: String) = either {
    coroutineScope {
        val stopTimesDeferred = async {
            withTimeoutOrNull(EnvVariables.timeoutSeconds) {
                getStopTimesResponse(stopCode).getOrNull()
            }
        }

        val avanzaTimesDeferred = async { getAvanzaData(getStopCodeFromFullStopCode(stopCode)) }

        val result = extractCRTMStopTimes(
            stopTimesDeferred.await(),
            getCoordinatesByStopCode(stopCode).bind(),
            getStopNameByStopCode(stopCode).getOrNull(),
            getStopCodeFromFullStopCode(stopCode)
        ).let { extractAndMergeAvanzaBuses(avanzaTimesDeferred.await(), it) }

        result
    }
}