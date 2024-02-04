package api.extensions

import api.exceptions.BusTrackerException
import api.exceptions.BusTrackerException.JsonError
import arrow.core.Either
import arrow.core.continuations.EffectScope
import arrow.core.getOrElse
import simpleJson.exceptions.JsonException

context(EffectScope<BusTrackerException>)
suspend fun <T> Either<JsonException, T>.bindJson() = mapLeft { JsonError(it.message) }.bind()

fun <T : Exception, K> Either<T, K>.getOrThrow() = getOrElse { throw it }
