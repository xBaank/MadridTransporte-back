package api.utils

import api.exceptions.BusTrackerException
import api.routing.stops.StopTimes
import arrow.core.Either
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

typealias Pipeline = PipelineContext<Unit, ApplicationCall>
typealias StopTimesF = suspend (String) -> Either<BusTrackerException, StopTimes>