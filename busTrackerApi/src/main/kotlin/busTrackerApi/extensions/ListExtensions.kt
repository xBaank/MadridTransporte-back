package busTrackerApi.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

suspend fun <A> List<A>.onEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
    this@onEachAsync
}

suspend fun <K, V> Map<K, V>.forEachAsync(f: suspend (Map.Entry<K, V>) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
    this@forEachAsync
}

suspend fun <A, T> Collection<A>.mapAsync(f: suspend (A) -> T) = coroutineScope {
    map { async { f(it) } }
}

suspend fun <A> Collection<A>.forEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
}