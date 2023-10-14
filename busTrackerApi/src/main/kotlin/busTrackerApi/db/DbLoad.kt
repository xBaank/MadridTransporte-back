package busTrackerApi.db

import arrow.core.continuations.either
import busTrackerApi.config.EnvVariables
import busTrackerApi.config.httpClient
import busTrackerApi.config.stopsCollection
import busTrackerApi.config.stopsInfoCollection
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import com.mongodb.client.model.Filters
import ru.gildor.coroutines.okhttp.await
import simpleJson.asArray
import simpleJson.asJson
import simpleJson.deserialized

suspend fun loadDataIntoDb() = either {
    val allStopsUrl = EnvVariables.allStopsUrl
    val allStopsInfoUrl = EnvVariables.allStopsInfoUrl

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
