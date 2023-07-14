package busTrackerApi.extensions

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException
import simpleJson.exceptions.JsonException

fun <T> Either<JsonException, T>.toBusTrackerException() = mapLeft { BusTrackerException.JsonError(it.message) }