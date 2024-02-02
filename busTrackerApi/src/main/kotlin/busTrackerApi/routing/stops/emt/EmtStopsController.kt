package busTrackerApi.routing.stops.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.db.getCoordinatesByStopCode
import busTrackerApi.db.getStopNameByStopCode
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindJson
import busTrackerApi.extensions.get
import busTrackerApi.extensions.post
import crtm.utils.getStopCodeFromFullStopCode
import ru.gildor.coroutines.okhttp.await
import simpleJson.JsonNode
import simpleJson.deserialized
import simpleJson.jObject
import java.text.SimpleDateFormat
import java.util.*

lateinit var currentLoginResponse: LoginResponse
private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

suspend fun login() = either {
    val url = "https://openapi.emtmadrid.es/v1/mobilitylabs/user/login/"

    //Hardcoded gojo limitless token
    val response = httpClient.get(
        url, mapOf(
            "passKey" to "504fea88211f2f90633f964189b7696037d65cc3a5f47b8fa1d5ea5e34db0239ad2e068851e72be0cec125779224749e3bc236c1b7af39d8a3d398e99223f058",
            "X-ClientId" to "428B01E6-693C-4F7F-A11E-3BB923420587",
        )
    ).await()

    if (!response.isSuccessful) shift<Nothing>(InternalServerError("EMT login failed"))
    val body =
        response.body?.string()?.deserialized()?.bindJson() ?: shift<Nothing>(InternalServerError("Body is null"))
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

        }, mapOf("accessToken" to currentLoginResponse.accessToken)).await()

        response.use {
            if (it.code == 404) shift<NotFound>(NotFound("Stop not found"))

            if (it.code == 401 || it.code == 403) {
                login().bind()
                tries--
                return@use
            }
            if (!it.isSuccessful) return@either null

            return@either it.body?.string()?.deserialized()?.bindJson()
        }

    } while (tries > 0)

    shift<Nothing>(InternalServerError("EMT getStopTimes failed"))
}

suspend fun getEmtStopTimes(stopCode: String) = either {
    val response = getEmtStopTimesResponse(stopCode).bind()
    response?.let { extractEMTStopTimes(it).bind() } ?: createEMTFailedTimes(
        name = getStopNameByStopCode(stopCode).bind(),
        coordinates = getCoordinatesByStopCode(stopCode).bind(),
        stopCode = getStopCodeFromFullStopCode(stopCode)
    )
}