package busTrackerApi

import io.github.cdimascio.dotenv.Dotenv

fun Dotenv.getenvOrThrow(key: String): String =
    get(key) ?: throw IllegalStateException("Environment variable $key is not set")