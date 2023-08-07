package busTrackerApi.extensions

import simpleJson.jObject

fun Map<String, String>.asJson() = jObject {
    forEach { (k, v) ->
        k.trim() += v
    }
}