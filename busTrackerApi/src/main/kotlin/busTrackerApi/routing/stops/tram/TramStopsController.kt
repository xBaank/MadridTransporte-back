package busTrackerApi.routing.stops.tram

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.timed
import io.github.reactivecircus.cache4k.Cache
import ru.gildor.coroutines.okhttp.await
import simpleJson.JsonNode
import simpleJson.deserialized
import simpleJson.jArray
import java.util.*
import kotlin.time.Duration.Companion.hours

private val stopTimesCache = Cache
    .Builder()
    .expireAfterWrite(1.hours)
    .build<String, TimedCachedValue<JsonNode>>()

private fun urlLasTablas(stop: String) = "http://www.metrosligerosdemadrid.es/wp-content/themes/ml1/js/horarios/las_tablas/summer/l-v/$stop.json"
private fun urlPinarDeChamartin(stop: String) = "http://www.metrosligerosdemadrid.es/wp-content/themes/ml1/js/horarios/pinar_de_chamartin/summer/l-v/$stop.json"


suspend fun getTramTimesResponse(stopName: String) = either {
    val fixedName = stopName.replace(" ", "_").lowercase(Locale.getDefault())
    val response1 = httpClient.get(urlLasTablas(fixedName)).await()
    val response2 = httpClient.get(urlPinarDeChamartin(fixedName)).await()

    val json =
        if(response1.isSuccessful && response1.header("Content-Type") == "application/json")
            response1.body?.string()?.deserialized()?.bindMap()
        else null
    val json2 =
        if(response2.isSuccessful && response2.header("Content-Type") == "application/json")
            response2.body?.string()?.deserialized()?.bindMap()
        else null

    val result = jArray {
        if(json != null) add(json)
        if(json2 != null) add(json2)
    }.timed()

    stopTimesCache.put(stopName, result)
    result
}

suspend fun getTramTimesResponseCached(stopName: String) = either {
    stopTimesCache.get(stopName) ?: shift(NotFound("No cached data"))
}