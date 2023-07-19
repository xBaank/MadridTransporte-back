package busTrackerApi.routing.stops

import arrow.core.continuations.either
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import io.ktor.http.*
import io.ktor.server.application.*

suspend fun Call.getStopsByQuery() = either {
    val query = call.request.queryParameters.getWrapped("search").bind()

    val stops = getStopsByQuery(query).bind()

    ResponseJson(buildStopsJson(stops), HttpStatusCode.OK)
}