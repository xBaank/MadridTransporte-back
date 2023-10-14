package busTrackerApi.db

import arrow.core.continuations.either
import busTrackerApi.config.httpClient
import busTrackerApi.config.stopsCollection
import busTrackerApi.config.stopsInfoCollection
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import busTrackerApi.utils.getenvOrNull
import com.mongodb.client.model.Filters
import ru.gildor.coroutines.okhttp.await
import simpleJson.asArray
import simpleJson.asJson
import simpleJson.deserialized

private const val allStopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/stops.json"
private const val allStopsInfoUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/stops-info.json"

suspend fun loadDataIntoDb() = either {
    val allStopsUrl = getenvOrNull("ALL_STOPS_URL") ?: allStopsUrl
    val allStopsInfoUrl = getenvOrNull("METRO_TRAIN_STOPS_URL") ?: allStopsInfoUrl

    val allStops = parseStops(getAsJson(allStopsUrl).bind()).bind()
    val allStopsInfo = parseStopsInfo(getAsJson(allStopsInfoUrl).bind()).bind()

    stopsCollection.deleteMany(filter = Filters.empty())
    stopsInfoCollection.deleteMany(filter = Filters.empty())

    stopsCollection.insertMany(allStops)
    stopsInfoCollection.insertMany(allStopsInfo)
}


private suspend fun getAsJson(url: String) = either {
    val response = httpClient.get(url).await().use { response ->
        val json =
            response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().asArray().bindMap().asJson()
    }
    response
}
