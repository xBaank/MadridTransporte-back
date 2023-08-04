package busTrackerApi.utils

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException.InternalServerError

fun getenvOrNull(key: String): String? =
    System.getenv(key) ?: System.getProperty(key) ?: null

fun getenvWrapped(key: String): Either<InternalServerError, String> =
    getenvOrNull(key)?.right() ?: InternalServerError("Environment variable $key not found").left()