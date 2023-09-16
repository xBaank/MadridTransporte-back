@file:OptIn(DelicateCoroutinesApi::class)

package busTrackerApi.utils

import crtm.abono.VentaPrepagoTitulo
import crtm.soap.AuthHeader
import crtm.soap.MultimodalInformation
import crtm.soap.MultimodalInformation_Service
import crtm.soap.PublicKeyRequest
import crtm.utils.authHeader
import kotlinx.coroutines.*


val defaultClient =
    GlobalScope.async(Dispatchers.IO, start = CoroutineStart.LAZY) { MultimodalInformation_Service().basicHttp }
val abonoClient = GlobalScope.async(
    Dispatchers.IO,
    start = CoroutineStart.LAZY
) { VentaPrepagoTitulo().basicHttpBindingIVentaPrepagoTitulo }

private val privateKey = "pruebapruebapruebapruebaprueba12".toByteArray()
suspend fun MultimodalInformation.auth(): AuthHeader = withContext(Dispatchers.IO) {
    val key = getPublicKey(PublicKeyRequest())
    authHeader(key.key.toByteArray(), privateKey)
}

