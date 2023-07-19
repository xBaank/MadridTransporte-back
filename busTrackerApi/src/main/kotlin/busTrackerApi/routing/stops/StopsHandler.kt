package busTrackerApi.routing.stops

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import io.ktor.http.*
import io.ktor.server.application.*

suspend fun Call.getStopsByQuery() = either {
    val query = call.request.queryParameters.getWrapped("query").bind()

    val stops = getStopsByQuery(query).bind()

    ResponseJson(buildStopsJson(stops), HttpStatusCode.OK)
}

suspend fun Call.getLocations() = either {
    val latitude = call.request.queryParameters.getWrapped("latitude").bind().toDoubleOrNull() ?:
    shift<Nothing>(BusTrackerException.BadRequest("Latitude must be a double"))
    val longitude = call.request.queryParameters.getWrapped("longitude").bind().toDoubleOrNull() ?:
    shift<Nothing>(BusTrackerException.BadRequest("Longitude must be a double"))

    val stops = getStopsByLocation(latitude, longitude).bind()

    ResponseJson(buildStopLocationsJson(stops), HttpStatusCode.OK)
}