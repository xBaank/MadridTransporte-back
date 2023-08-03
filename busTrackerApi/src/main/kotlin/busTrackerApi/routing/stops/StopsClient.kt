package busTrackerApi.routing.stops

import arrow.core.continuations.either
import arrow.core.getOrElse
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.removeNonSpacingMarks
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*
import simpleJson.asArray
import simpleJson.asJson
import simpleJson.asString
import simpleJson.get


suspend fun getAlertsByCodMode(codMode: String) = either {
    val alerts = getAlertsByCodModeResponse(codMode).bind()
    
    ResponseJson(buildAlertsJson(alerts.value), HttpStatusCode.OK)
}

suspend fun Call.getStopsByQuery() = either {
    val query = call.request.queryParameters.getWrapped("query").bind().removeNonSpacingMarks()

    val stops = getAllStopsResponse().bind().asArray().bindMap()
        .filter { it["DENOMINACION"].asString().bindMap().removeNonSpacingMarks().contains(query, ignoreCase = true) }
        .asJson()

    ResponseJson(stops, HttpStatusCode.OK)
}

suspend fun Call.getStopsByLocation() = either {
    val latitude = call.request.queryParameters.getWrapped("latitude").bind().toDoubleOrNull() ?:
    shift<Nothing>(BusTrackerException.BadRequest("Latitude must be a double"))
    val longitude = call.request.queryParameters.getWrapped("longitude").bind().toDoubleOrNull() ?:
    shift<Nothing>(BusTrackerException.BadRequest("Longitude must be a double"))

    val stops = getStopsByLocationResponse(latitude, longitude).bind()

    ResponseJson(buildStopLocationsJson(stops), HttpStatusCode.OK)
}

suspend fun getAllStops() = either {
    val stops = getAllStopsResponse().bind()
    ResponseJson(stops, HttpStatusCode.OK)
}

suspend fun Call.getStopTimes(codMode : String) = either {
    val stopCode = createStopCode(codMode, call.parameters.getWrapped("stopCode").bind())

    val cached =  getTimesResponse(stopCode, codMode)
        .onRight { stopTimesCache.put(stopCode, it) }
        .getOrElse { stopTimesCache.get(stopCode) } ?:
        shift<Nothing>(BusTrackerException.NotFound("No stop times found for stop code $stopCode"))

    val json = buildCachedJson(cached.value.let(::buildStopTimesJson), cached.createdAt.toEpochMilli())

    ResponseJson(json, HttpStatusCode.OK)
}

suspend fun Call.getStopTimesCached(codMode: String) = either {
    val stopCode = createStopCode(codMode, call.parameters.getWrapped("stopCode").bind())
    val cached = stopTimesCache.get(stopCode) ?: shift<Nothing>(BusTrackerException.NotFound("Stop code not found"))

    val json = buildCachedJson(cached.value.let(::buildStopTimesJson), cached.createdAt.toEpochMilli())

    ResponseJson(json, HttpStatusCode.OK)
}

