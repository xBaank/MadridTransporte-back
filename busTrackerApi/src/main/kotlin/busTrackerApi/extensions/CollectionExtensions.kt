package busTrackerApi.extensions

import kotlinx.coroutines.*

suspend fun <A> Collection<A>.forEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
}

suspend fun <A, B> Collection<A>.mapAsync(f: suspend (A) -> B) = coroutineScope {
    map { async { f(it) } }.awaitAll()
}