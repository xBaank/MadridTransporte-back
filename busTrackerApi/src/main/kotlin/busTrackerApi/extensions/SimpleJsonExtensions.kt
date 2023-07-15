package busTrackerApi.extensions

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException.JsonError
import simpleJson.exceptions.JsonException

fun <T> Either<JsonException, T>.toBusTrackerException() = mapLeft { JsonError(it.message) }