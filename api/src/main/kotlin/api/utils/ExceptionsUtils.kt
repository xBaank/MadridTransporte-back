package api.utils

import api.exceptions.BusTrackerException
import io.ktor.util.logging.*

private val logger = KtorSimpleLogger("Soap")

val mapExceptionsF: (Throwable) -> BusTrackerException = { it ->
    logger.error(it)
    if (it is BusTrackerException) it
    else BusTrackerException.SoapError(it.message)
}