package api.extensions

import common.extensions.forEachAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.batched(size: Int) = flow {
    val batch = mutableListOf<T>()
    collect {
        batch.add(it)
        if (batch.size == size) {
            emit(batch.toList())
            batch.clear()
        }
    }
    if (batch.isNotEmpty()) emit(batch.toList())
}

suspend fun <T> Flow<List<T>>.forEachAsync(f: suspend (T) -> Unit) = collect { batch ->
    batch.forEachAsync { f(it) }
}