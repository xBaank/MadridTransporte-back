package busTrackerApi.routing.stops

import arrow.core.Either
import busTrackerApi.config.EnvVariables.timeoutSeconds
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.getSuspend
import busTrackerApi.utils.auth
import busTrackerApi.utils.defaultClient
import busTrackerApi.utils.mapExceptionsF
import crtm.soap.ArrayOfString
import crtm.soap.IncidentsAffectationsRequest
import crtm.soap.IncidentsAffectationsResponse
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.hours

val cachedAlerts = Cache.Builder()
    .expireAfterWrite(24.hours)
    .build<String, IncidentsAffectationsResponse>()

suspend fun getAlertsByCodModeResponse(codMode: String) = Either.catch {
    val result = withTimeoutOrNull(timeoutSeconds) {
        val request = IncidentsAffectationsRequest().apply {
            this.codMode = codMode
            codLines = ArrayOfString()
            authentication = defaultClient.value().auth()
        }

        getSuspend(request, defaultClient.value()::getIncidentsAffectationsAsync)
    }

    if (result == null) {
        val cached = cachedAlerts.get(codMode)
        if (cached != null) return@catch cached
        else throw BusTrackerException.InternalServerError("Got empty response")
    }

    cachedAlerts.put(codMode, result)
    result
}.mapLeft(mapExceptionsF)

