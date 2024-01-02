package busTrackerApi.extensions

fun String.toDirection() = if (this == "A") 2 else 1