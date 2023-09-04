package busTrackerApi.routing.stops.metro

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getWrapped
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.TimedCachedValue
import busTrackerApi.routing.stops.buildCachedJson
import busTrackerApi.routing.stops.buildJson
import busTrackerApi.utils.Call
import crtm.utils.createStopCode
import io.ktor.http.*
import io.ktor.server.application.*

suspend fun Call.getMetroTimes(codMode: String) =
    getMetroTimesBase(::busTrackerApi.routing.stops.metro.getMetroTimesResponse, codMode, call.parameters.getWrapped("stopCode"))

suspend fun Call.getMetroTimesCached(codMode: String) =
    getMetroTimesBase(::getMetroTimesResponseCached, codMode, call.parameters.getWrapped("stopCode"))

private suspend fun getMetroTimesBase(
    f: suspend (String, String) -> Either<BusTrackerException, TimedCachedValue<StopTimes>>,
    codMode: String,
    id: Either<BusTrackerException, String>
) = either {
    val stopCode = createStopCode(codMode, id.bind())
    val json = f(stopCode, codMode).bind()
    ResponseJson(buildCachedJson(buildJson(json.value), json.createdAt.toEpochMilli()), HttpStatusCode.OK)
}


