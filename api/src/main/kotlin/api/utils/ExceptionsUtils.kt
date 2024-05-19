package api.utils

import common.exceptions.BusTrackerException


val mapExceptionsF: (Throwable) -> BusTrackerException = { it ->
    if (it is BusTrackerException) it
    else BusTrackerException.SoapError(it.message)
}