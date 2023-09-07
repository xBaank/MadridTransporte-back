package busTrackerApi.extensions

import io.github.reactivecircus.cache4k.Cache
import simpleJson.JsonNode
import simpleJson.serialized

private val cacheJson = Cache.Builder()
    .build<JsonNode, String>()

suspend fun JsonNode.serializedMemo() = cacheJson.get(this) {
    serialized()
}