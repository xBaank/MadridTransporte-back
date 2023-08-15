package busTrackerApi.extensions

import arrow.core.Either
import arrow.core.continuations.EffectScope
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.JsonError
import busTrackerApi.routing.Response
import busTrackerApi.utils.Call
import simpleJson.JsonNode
import simpleJson.asNumber
import simpleJson.asString
import simpleJson.exceptions.JsonException

context(EffectScope<BusTrackerException>)
suspend fun <T> Either<JsonException, T>.bindMap() = mapLeft { JsonError(it.message) }.bind()

suspend inline fun Call.handle(f: () -> Either<BusTrackerException, Response>) =
    f().fold({ handleError(it) }, { handleResponse(it) })

context(EffectScope<BusTrackerException>)
suspend fun Either<JsonException, JsonNode>.asNumberOrString() =
    asNumber().getOrNull()?.toString() ?: asString().bindMap()
