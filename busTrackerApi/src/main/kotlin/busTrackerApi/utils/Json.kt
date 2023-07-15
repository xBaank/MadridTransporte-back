package busTrackerApi.utils

import simpleJson.jObject

fun errorObject(message: String) = jObject {
    "message" += message
}

fun accessTokenObject(token: String) = jObject {
    "token" += token
}