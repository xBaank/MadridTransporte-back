package busTrackerApi.extensions

import arrow.core.Either
import arrow.core.continuations.EffectScope
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.JsonError
import busTrackerApi.exceptions.CloseSocketException
import busTrackerApi.routing.Response
import busTrackerApi.utils.Call
import io.ktor.websocket.*
import simpleJson.exceptions.JsonException

context(EffectScope<BusTrackerException>)
suspend fun <T> Either<JsonException, T>.bindMap() = mapLeft { JsonError(it.message) }.bind()

fun <T> Either<BusTrackerException, T>.toCloseSocketException(reason: CloseReason.Codes) = mapLeft { CloseSocketException(it.message, reason) }
suspend inline fun Call.handle(f : () -> Either<BusTrackerException, Response>) = f().fold({ handleError(it) }, { handleResponse(it) })
suspend inline fun DefaultWebSocketSession.handle(f : () -> Either<CloseSocketException, Unit>) = f().fold({ handleError(it) }, {})
