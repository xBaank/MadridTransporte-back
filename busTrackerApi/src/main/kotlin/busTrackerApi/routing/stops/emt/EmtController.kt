package busTrackerApi.routing.stops.emt

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.InternalServerError
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.extensions.post
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.timed
import crtm.utils.getCodStopFromStopCode
import io.github.reactivecircus.cache4k.Cache
import ru.gildor.coroutines.okhttp.await
import simpleJson.deserialized
import simpleJson.jObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.hours

val stopTimesCache = Cache.Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<StopTimes>>()

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
    val body = response.body?.string()?.deserialized()?.bindMap() ?: shift<Nothing>(InternalServerError("Body is null"))
    currentLoginResponse = parseLoginResponse(body).bindMap()
}

suspend fun getStopTimesResponse(stopCode: String) = either {
    val stopId = getCodStopFromStopCode(stopCode)
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

        if (response.code == 404) shift<NotFound>(NotFound("Stop not found"))

        if (response.code == 401 || response.code == 403) {
            login().bind()
            tries--
            continue
        }

        if (!response.isSuccessful) shift<Nothing>(InternalServerError("EMT getStopTimes failed"))
        val body =
            response.body?.string()?.deserialized()?.bindMap() ?: shift<Nothing>(InternalServerError("Body is null"))
        val result = parseEMTToStopTimes(body).bind().timed()
        stopTimesCache.put(stopId, result)
        return@either result

    } while (tries > 0)

    shift<Nothing>(InternalServerError("EMT getStopTimes failed"))
}

suspend fun getStopTimesResponseCached(stopId: String) = either {
    stopTimesCache.get(stopId) ?: shift<Nothing>(NotFound("No stop times found for stop $stopId"))
}