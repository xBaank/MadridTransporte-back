package common

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import common.exceptions.BusTrackerException

object EnvUtils {
    fun getenvOrNull(key: String): String? =
        System.getenv(key) ?: System.getProperty(key) ?: null

    fun getenvWrapped(key: String): Either<BusTrackerException.InternalServerError, String> =
        getenvOrNull(key)?.right() ?: BusTrackerException.InternalServerError("Environment variable $key not found")
            .left()
}