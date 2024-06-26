package api.routing.stops.emt

import api.config.httpClient
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.resilience.Schedule
import arrow.resilience.retry
import common.exceptions.BusTrackerException
import common.exceptions.BusTrackerException.InternalServerError
import common.exceptions.BusTrackerException.NotFound
import common.extensions.awaitWrap
import common.extensions.bindJson
import common.extensions.get
import common.extensions.post
import common.queries.getCoordinatesByStopCode
import common.queries.getStopNameByStopCode
import common.utils.getStopCodeFromFullStopCode
import io.ktor.util.logging.*
import ru.gildor.coroutines.okhttp.await
import simpleJson.JsonNode
import simpleJson.deserialized
import simpleJson.jObject
import java.io.InterruptedIOException
import java.text.SimpleDateFormat
import java.util.*

lateinit var currentLoginResponse: LoginResponse
private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
private val logger = KtorSimpleLogger("EmtControllerLogger")

suspend fun login() = either {
    val url = "https://openapi.emtmadrid.es/v1/mobilitylabs/user/login/"

    //🤫🧏🏻‍♂️
    val response = try {
        httpClient.get(
            url, mapOf(
                "passKey" to "504fea88211f2f90633f964189b7696037d65cc3a5f47b8fa1d5ea5e34db0239ad2e068851e72be0cec125779224749e3bc236c1b7af39d8a3d398e99223f058",
                "X-ClientId" to "428B01E6-693C-4F7F-A11E-3BB923420587",
            )
        ).await()
    } catch (ex: InterruptedIOException) {
        raise(InternalServerError("Emt timeout error"))
    }

    if (!response.isSuccessful) raise(InternalServerError("EMT login failed"))
    val body =
        response.body?.string()?.deserialized()?.bindJson() ?: raise(InternalServerError("Body is null"))
    currentLoginResponse = parseLoginResponse(body).bindJson()
}

suspend fun getEmtStopTimesResponse(stopCode: String): Either<BusTrackerException, JsonNode?> = either {
    val stopId = getStopCodeFromFullStopCode(stopCode)
    var tries = 3

    do {
        val url = "https://openapi.emtmadrid.es/v2/transport/busemtmad/stops/$stopId/arrives/"

        val response = httpClient.post(url, jObject {
            "cultureInfo" += "ES"
            "Text_StopRequired_YN" += "Y"
            "Text_EstimationsRequired_YN" += "Y"
            "Text_IncidencesRequired_YN" += "Y"
            "DateTime_Referenced_Incidencies_YYYYMMDD" += dateFormatter.format(Date())

        }, mapOf("accessToken" to currentLoginResponse.accessToken)).awaitWrap().getOrNull()


        response?.use {
            if (it.code == 404) raise(NotFound("Stop not found"))
            if (it.code == 401 || it.code == 403) {
                login().getOrElse(logger::error)
                return@use
            }
            if (!it.isSuccessful) return@use

            return@either it.body?.string()?.deserialized()?.bindJson()
        }

        tries--
    } while (tries > 0)

    null
}

suspend fun getEmtStopTimes(stopCode: String) = either {
    retry(Schedule.recurs(5)) {
        val response = getEmtStopTimesResponse(stopCode).bind()
        if (response != null) return@either extractEMTStopTimes(response).bind()
        createEMTFailedTimes(
            name = getStopNameByStopCode(stopCode).bind(),
            coordinates = getCoordinatesByStopCode(stopCode).bind(),
            stopCode = getStopCodeFromFullStopCode(stopCode)
        )
    }
}