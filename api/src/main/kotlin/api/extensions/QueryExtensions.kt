package api.extensions

import api.exceptions.BusTrackerException
import arrow.core.left
import arrow.core.right
import io.ktor.http.*

fun Parameters.getWrapped(key: String) =
    get(key)?.right() ?: BusTrackerException.QueryParamError("Missing query parameter $key").left()