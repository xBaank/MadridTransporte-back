package busTrackerApi.utils

fun getenvOrNull(key: String): String? =
    System.getenv(key) ?: System.getProperty(key) ?: null