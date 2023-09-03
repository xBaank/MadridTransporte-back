package busTrackerApi.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

suspend fun <A> Collection<A>.onEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    forEachAsync { f(it) }
    this@onEachAsync
}
suspend fun <A> Collection<A>.forEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
}