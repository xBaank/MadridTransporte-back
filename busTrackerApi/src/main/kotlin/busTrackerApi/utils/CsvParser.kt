package busTrackerApi.utils

import simpleJson.JsonArray
import simpleJson.asJson
import simpleJson.jObject

//Use json syntax to simplify its usage
fun parse(source: String): JsonArray {
    //TODO There is a bug here
    val headers = source.split("\n")[0].split(",")
    val rows = source.split("\n").drop(1).map { it.split(",", limit = headers.size) }.filter { it.size == headers.size }
    return rows.map { row ->
        val obj = jObject {
            headers.forEachIndexed { index, header ->
                val value = row.getOrNull(index) ?: return@forEachIndexed
                header += value
            }
        }
        obj
    }.asJson()
}