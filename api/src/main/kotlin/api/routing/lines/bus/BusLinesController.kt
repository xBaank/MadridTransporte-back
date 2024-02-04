package api.routing.lines.bus

import api.config.EnvVariables.timeoutSeconds
import api.config.httpClient
import api.db.models.ItineraryWithStops
import api.exceptions.BusTrackerException.NotFound
import api.exceptions.BusTrackerException.SoapError
import api.extensions.get
import api.extensions.getSuspend
import api.extensions.unzip
import api.utils.auth
import api.utils.defaultClient
import api.utils.fixColor
import api.utils.mapExceptionsF
import arrow.core.Either
import arrow.core.continuations.either
import crtm.soap.ArrayOfString
import crtm.soap.LineInformationRequest
import crtm.soap.LineLocationRequest
import crtm.utils.getCodModeFromLineCode
import kotlinx.coroutines.withTimeoutOrNull
import ru.gildor.coroutines.okhttp.await

suspend fun getLocationsResponse(itinerary: ItineraryWithStops, lineCode: String, codMode: String, stopCode: String?) =
    Either.catch {
        val lineRequest = LineLocationRequest().apply {
            this.codMode = codMode
            codLine = lineCode
            codItinerary = itinerary.itineraryCode
            direction = itinerary.direction + 1
            authentication = defaultClient.value().auth()
            codStop = stopCode ?: "8_"
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLineLocationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)

private suspend fun getLineInfoResponse(lineCode: String) =
    Either.catch {
        val lineRequest = LineInformationRequest().apply {
            codLine = ArrayOfString().apply { string.add(lineCode) }
            activeItinerary = 1
            authentication = defaultClient.value().auth()
        }
        withTimeoutOrNull(timeoutSeconds) {
            getSuspend(lineRequest, defaultClient.value()::getLinesInformationAsync)
        } ?: throw SoapError("Server error")
    }.mapLeft(mapExceptionsF)

suspend fun getKmlText(itinerary: ItineraryWithStops) = either {
    val lineInformationResponse = getLineInfoResponse(itinerary.fullLineCode).bind()

    val kml = lineInformationResponse
        .lines
        .lineInformation
        .firstOrNull()
        ?.itinerary
        ?.itinerary
        ?.firstOrNull { it.codItinerary == itinerary.itineraryCode || it.direction == itinerary.direction + 1 }
        ?.kml
        ?: shift<Nothing>(NotFound())

    val data = httpClient.get(kml).await()
    val unzipped = data.body
        ?.source()
        ?.inputStream()
        ?.unzip()
        ?.values
        ?.firstOrNull()
        ?: shift<Nothing>(NotFound())

    fixColor(getCodModeFromLineCode(itinerary.fullLineCode), unzipped)
}