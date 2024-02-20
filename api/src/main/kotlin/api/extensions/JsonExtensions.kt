package api.extensions

import io.github.reactivecircus.cache4k.Cache
import simpleJson.JsonNode
import simpleJson.serialized
import kotlin.time.Duration.Companion.hours

private val cacheJson = Cache.Builder<JsonNode, String>()
    .expireAfterWrite(24.hours)
    .build()

suspend fun JsonNode.serializedMemo() = cacheJson.get(this) {
    serialized()
}