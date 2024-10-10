package api.utils

import api.routing.stops.StopTimes
import arrow.core.Either
import common.exceptions.BusTrackerException
import io.ktor.server.routing.*

typealias Pipeline = RoutingContext
typealias StopTimesF = suspend (String) -> Either<BusTrackerException, StopTimes>