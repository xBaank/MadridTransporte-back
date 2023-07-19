package busTrackerApi.routing.stops

import arrow.core.Either
import busTrackerApi.utils.mapExceptionsF
import crtm.auth
import crtm.defaultClient
import crtm.soap.*
import simpleJson.jArray

fun getStopsByQuery(query: String) = Either.catch {
    val request = StopRequestV2().apply {
        customSearch = query
        authentication = defaultClient.auth()
    }
    defaultClient.getStopsV2(request)
}.mapLeft(mapExceptionsF)

fun getStopsByLocation(lat: Double, lon: Double) = Either.catch {
    val request = StopsByGeoLocationRequest().apply {
        coordinates = Coordinates().apply {
            latitude = lat
            longitude = lon
        }
        method = 1
        precision = 1000
        codMode = "8"
        authentication = defaultClient.auth()
    }
    defaultClient.getNearestStopsByGeoLocation(request)
}.mapLeft(mapExceptionsF)

fun buildStopsJson(stops: StopResponse) = jArray {
    stops.stops.stop.forEach { stop ->
        addObject {
            "codStop" += stop.codStop
            "codMode" += stop.codMode
            "name" += stop.name
            "latitude" += stop.coordinates.latitude
            "longitude" += stop.coordinates.longitude
        }
    }
}

fun buildStopLocationsJson(stops: StopsByGeoLocationResponse) = jArray {
    stops.stops.stop.forEach { stop ->
        addObject {
            "codStop" += stop.codStop
            "codMode" += stop.codMode
            "name" += stop.name
            "latitude" += stop.coordinates.latitude
            "longitude" += stop.coordinates.longitude
        }
    }
}