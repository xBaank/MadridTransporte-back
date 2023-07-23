package busTrackerApi.extensions

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.JsonError
import busTrackerApi.exceptions.CloseSocketException
import busTrackerApi.routing.Response
import busTrackerApi.utils.Call
import io.ktor.websocket.*
import simpleJson.exceptions.JsonException

fun <T> Either<JsonException, T>.toBusTrackerException() = mapLeft { JsonError(it.message) }
fun <T> Either<BusTrackerException, T>.toCloseSocketException(reason: CloseReason.Codes) = mapLeft { CloseSocketException(it.message, reason) }
context(Call)
suspend fun Either<BusTrackerException, Response>.handle() = fold({ handleError(it) }, { handleResponse(it) })
context(DefaultWebSocketSession)
suspend fun Either<CloseSocketException,Unit>.handle() = fold({ handleError(it) }, {})
