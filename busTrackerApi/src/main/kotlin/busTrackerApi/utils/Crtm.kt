@file:OptIn(DelicateCoroutinesApi::class)

package busTrackerApi.utils

import busTrackerApi.extensions.getSuspend
import crtm.abono.VentaPrepagoTitulo
import crtm.soap.AuthHeader
import crtm.soap.MultimodalInformation
import crtm.soap.MultimodalInformation_Service
import crtm.soap.PublicKeyRequest
import crtm.utils.authHeader
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

const val allStopsUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/stops.json"
const val allStopsInfoUrl = "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/stops-info.json"
val timeoutSeconds = 30.seconds

val defaultClient = SuspendingLazy {
    withContext(Dispatchers.IO) {
        MultimodalInformation_Service().apply {
            executor = Dispatchers.IO.asExecutor()
        }.basicHttp
    }
}
val abonoClient =
    SuspendingLazy {
        withContext(Dispatchers.IO) {
            VentaPrepagoTitulo().apply {
                executor = Dispatchers.IO.asExecutor()
            }.basicHttpBindingIVentaPrepagoTitulo
        }
    }

private val privateKey = "pruebapruebapruebapruebaprueba12".toByteArray()
suspend fun MultimodalInformation.auth(): AuthHeader {
    val key = getSuspend(PublicKeyRequest(), ::getPublicKeyAsync)
    return authHeader(key.key.toByteArray(), privateKey)
}

