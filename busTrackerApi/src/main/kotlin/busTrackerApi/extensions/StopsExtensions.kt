package busTrackerApi.extensions

import jakarta.xml.ws.AsyncHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Future
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend inline fun <T, R : Any?> getSuspend(
    request: T,
    crossinline f: (T, AsyncHandler<R>) -> Future<*>
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

