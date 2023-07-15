package busTrackerApi.extensions

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.JsonError
import busTrackerApi.exceptions.CloseSocketException
import io.ktor.websocket.*
import simpleJson.exceptions.JsonException

fun <T> Either<JsonException, T>.toBusTrackerException() = mapLeft { JsonError(it.message) }
fun <T> Either<BusTrackerException, T>.toCloseSocketException(reason: CloseReason.Codes) = mapLeft { CloseSocketException(it.message, reason) }
