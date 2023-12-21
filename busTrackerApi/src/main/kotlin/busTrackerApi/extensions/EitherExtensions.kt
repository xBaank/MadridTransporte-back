package busTrackerApi.extensions

import arrow.core.Either
import arrow.core.continuations.EffectScope
import arrow.core.getOrElse
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.JsonError
import simpleJson.JsonNode
import simpleJson.asNumber
import simpleJson.asString
import simpleJson.exceptions.JsonException

context(EffectScope<BusTrackerException>)
suspend fun <T> Either<JsonException, T>.bindMap() = mapLeft { JsonError(it.message) }.bind()

context(EffectScope<BusTrackerException>)
suspend fun Either<JsonException, JsonNode>.asNumberOrString() =
    asNumber().getOrNull()?.toString() ?: asString().bindMap()

fun <T : Exception, K> Either<T, K>.getOrThrow() = getOrElse { throw it }
