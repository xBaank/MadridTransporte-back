package api.routing.lines

import api.config.EnvVariables.timeoutSeconds
import api.exceptions.BusTrackerException.SoapError
import api.extensions.getSuspend
import api.utils.auth
import api.utils.defaultClient
import api.utils.mapExceptionsF
import arrow.core.Either
import crtm.soap.LineLocationRequest
import kotlinx.coroutines.withTimeoutOrNull

suspend fun getLocationsResponse(lineCode: String, direction: Int, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            this.direction = direction
            authentication = defaultClient.value().auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)