package busTrackerApi.extensions

import arrow.core.Either
import arrow.core.continuations.EffectScope
import arrow.core.getOrElse
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.JsonError
import simpleJson.exceptions.JsonException

context(EffectScope<BusTrackerException>)
suspend fun <T> Either<JsonException, T>.bindJson() = mapLeft { JsonError(it.message) }.bind()

fun <T : Exception, K> Either<T, K>.getOrThrow() = getOrElse { throw it }
