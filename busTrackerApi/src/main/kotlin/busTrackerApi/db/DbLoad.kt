package busTrackerApi.db

import arrow.core.continuations.either
import busTrackerApi.config.*
import busTrackerApi.config.EnvVariables.reloadDb
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.get
import com.mongodb.client.model.Filters
import ru.gildor.coroutines.okhttp.await
import simpleJson.asArray
import simpleJson.asJson
import simpleJson.deserialized

suspend fun loadDataIntoDb() = either {
    if (!reloadDb) return@either

    val allStopsUrl = EnvVariables.allStopsUrl
    val allStopsInfoUrl = EnvVariables.allStopsInfoUrl
    val itinerariesUrl = EnvVariables.itinerariesUrl

    val allStops = parseStops(getAsJson(allStopsUrl).bind()).bind()
    val allStopsInfo = parseStopsInfo(getAsJson(allStopsInfoUrl).bind()).bind()
    val itineraries = parseItineraries(getAsJson(itinerariesUrl).bind()).bind()

    stopsCollection.deleteMany(filter = Filters.empty())
    stopsInfoCollection.deleteMany(filter = Filters.empty())
    itinerariesCollection.deleteMany(filter = Filters.empty())

    stopsCollection.insertMany(allStops)
    stopsInfoCollection.insertMany(allStopsInfo)
    itinerariesCollection.insertMany(itineraries)
}


private suspend fun getAsJson(url: String) = either {
    val response = httpClient.get(url).await().use { response ->
        val json =
            response.body?.string() ?: shift<Nothing>(BusTrackerException.InternalServerError("Got empty response"))
        json.deserialized().asArray().bindMap().asJson()
    }
    response
}
