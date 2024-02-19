package api.extensions

import api.exceptions.BusTrackerException
import api.exceptions.BusTrackerException.JsonError
import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.Raise
import simpleJson.exceptions.JsonException

context(Raise<BusTrackerException>)
fun <T> Either<JsonException, T>.bindJson() = mapLeft { JsonError(it.message) }.bind()

fun <T : Exception, K> Either<T, K>.getOrThrow() = getOrElse { throw it }
