package busTrackerApi

fun getenvOrThrow(key: String): String {
    return System.getenv(key) ?: throw IllegalStateException("Environment variable $key is not set")
}