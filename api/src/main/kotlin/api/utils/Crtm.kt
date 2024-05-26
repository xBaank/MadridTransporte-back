package api.utils

import com.sun.xml.ws.client.BindingProviderProperties
import common.utils.SuspendingLazy
import crtm.soap.AuthHeader
import crtm.soap.MultimodalInformation_Service
import crtm.soap.PublicKeyRequest
import io.github.reactivecircus.cache4k.Cache
import jakarta.xml.ws.AsyncHandler
import jakarta.xml.ws.BindingProvider
import korlibs.time.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Future
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val cachedAuthHeader = Cache.Builder<Unit, AuthHeader>()
    .expireAfterWrite(54.seconds)
    .build()

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

private val privateKey = "pruebapruebapruebapruebaprueba12".toByteArray()
private val ivParameterSpec = IvParameterSpec(ByteArray(16))

suspend fun auth(): AuthHeader = cachedAuthHeader.get(Unit) {
    val key = getSuspend(PublicKeyRequest(), defaultClient.value()::getPublicKeyAsync)
    authHeader(key.key.toByteArray())
}

private fun encrypt(inputText: ByteArray, key: SecretKeySpec): String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec)
    val cipherText = cipher.doFinal(inputText)
    return Base64.getEncoder().encodeToString(cipherText)
}

private fun authHeader(publicKey: ByteArray) = AuthHeader().apply {
    connectionKey = encrypt(publicKey, SecretKeySpec(privateKey, "AES"))
}

suspend inline fun <T, R : Any?> getSuspend(
    request: T,
    crossinline f: (T, AsyncHandler<R>) -> Future<*>,
): R =
    suspendCancellableCoroutine { continuation ->
        val future = f(request) { result ->
            try {
                continuation.resume(result.get())
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
        continuation.invokeOnCancellation { future.cancel(true) }
    }

