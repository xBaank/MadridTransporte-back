package busTrackerApi.utils

import simpleJson.jObject

fun errorObject(message: String) = jObject {
    "message" += message
}

