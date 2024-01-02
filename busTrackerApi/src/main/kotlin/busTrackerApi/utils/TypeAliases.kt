package busTrackerApi.utils

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.routing.stops.StopTimes
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

typealias Pipeline = PipelineContext<Unit, ApplicationCall>
typealias StopTimesF = suspend (String) -> Either<BusTrackerException, StopTimes>