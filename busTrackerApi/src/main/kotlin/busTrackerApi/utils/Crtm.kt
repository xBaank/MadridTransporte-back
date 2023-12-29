package busTrackerApi.utils

import busTrackerApi.extensions.getSuspend
import com.sun.xml.ws.client.BindingProviderProperties
import crtm.abono.VentaPrepagoTitulo
import crtm.soap.AuthHeader
import crtm.soap.MultimodalInformation
import crtm.soap.MultimodalInformation_Service
import crtm.soap.PublicKeyRequest
import crtm.utils.authHeader
import jakarta.xml.ws.BindingProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.withContext


val defaultClient = SuspendingLazy {
    withContext(Dispatchers.IO) {
        MultimodalInformation_Service().apply {
            executor = Dispatchers.IO.asExecutor()
        }.basicHttp.apply {
            (this as BindingProvider).requestContext[BindingProviderProperties.REQUEST_TIMEOUT] = 60_000
            (this as BindingProvider).requestContext[BindingProviderProperties.CONNECT_TIMEOUT] = 60_000
        }
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

