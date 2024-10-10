package api.extensions

import arrow.core.left
import arrow.core.right
import common.exceptions.BusTrackerException
import io.ktor.http.*

fun Parameters.getWrapped(key: String) =
    get(key)?.right() ?: BusTrackerException.QueryParamError("Missing query parameter $key").left()