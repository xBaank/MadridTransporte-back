package busTrackerApi.extensions

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

suspend fun <A> List<A>.onEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
    this@onEachAsync
}