package api.routing.lines

import api.config.EnvVariables.timeoutSeconds
import api.utils.auth
import api.utils.defaultClient
import api.utils.getSuspend
import api.utils.mapExceptionsF
import arrow.core.Either
import common.exceptions.BusTrackerException.SoapError
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

suspend fun getLocationsResponse(lineCode: String, itineraryCode: String, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            codItinerary = itineraryCode
            authentication = defaultClient.value().auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)