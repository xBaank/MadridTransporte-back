package busTrackerApi.extensions

import jakarta.xml.ws.AsyncHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend inline fun <T, R : Any?, K> getSuspend(
    request: T,
    crossinline f: (T, AsyncHandler<R>) -> K
): R =
    suspendCancellableCoroutine { continuation ->
        f(request) { result ->
            continuation.invokeOnCancellation { result.cancel(true) }
            try {
                continuation.resume(result.get())
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
    }

