package busTrackerApi.routing.lines.emt

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.routing.Response
import busTrackerApi.routing.Response.ResponseRaw
import busTrackerApi.utils.Pipeline
import io.ktor.http.*

suspend fun Pipeline.getLocations(): Either<BusTrackerException, Response> = either {
    ResponseRaw(HttpStatusCode.OK)
}