package busTrackerApi.utils

fun getenvOrThrow(key: String): String =
    System.getenv(key) ?: System.getProperty(key) ?: throw IllegalStateException("Environment variable $key is not set")

fun getenvOrNull(key: String): String? =
    System.getenv(key) ?: System.getProperty(key) ?: null