package busTrackerApi.extensions

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

suspend fun <T> ApiFuture<T>.await(): T {
    try {
        if (isDone) return get() as T
    } catch (e: ExecutionException) {
        throw e.cause ?: e // unwrap original cause from ExecutionException
    }

    return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        val callback = object : ApiFutureCallback<T> {
            override fun onSuccess(result: T) {
                cont.resume(result)
            }

            override fun onFailure(t: Throwable) {
                cont.resumeWithException(t)
            }
        }
        ApiFutures.addCallback(this, callback, MoreExecutors.directExecutor())
    }
}

suspend fun <T> Future<T>.wait(): T = coroutineScope {
    try {
        while (!isDone) {
            if (!isActive) this@wait.cancel(true)
            if (isCancelled) cancel()
            delay(0.5.seconds)
        }
        get()
    } catch (e: CancellationException) {
        this@wait.cancel(true)
        throw e
    }
}