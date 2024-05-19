package common.extensions

import kotlinx.coroutines.*
import java.util.*

suspend fun <A> Collection<A>.forEachAsync(f: suspend (A) -> Unit) = coroutineScope {
    map { launch { f(it) } }.joinAll()
}

suspend fun <A, B> Collection<A>.mapAsync(f: suspend (A) -> B) = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

fun <A> Iterable<A>.toEnumeration() = object : Enumeration<A> {
    private val iterator = this@toEnumeration.iterator()
    override fun hasMoreElements(): Boolean = iterator.hasNext()
    override fun nextElement(): A = iterator.next()
}