package api.extensions

import kotlinx.coroutines.*
import java.util.*

suspend fun <A> Collection<A>.parallelForEach(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
}

suspend fun <A, B> Collection<A>.parallelMap(f: suspend (A) -> B) = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

suspend fun <A> Sequence<A>.forEachAsync(chunkSize: Int, f: suspend (A) -> Unit) = coroutineScope {
    chunked(chunkSize).forEach { it.map { launch { f(it) } }.joinAll() }
}

fun <A> Iterable<A>.toEnumeration(): Enumeration<A> = object : Enumeration<A> {
    private val iterator = this@toEnumeration.iterator()
    override fun hasMoreElements(): Boolean = iterator.hasNext()
    override fun nextElement(): A = iterator.next()
}