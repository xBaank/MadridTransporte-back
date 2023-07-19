package busTrackerApi.utils

import busTrackerApi.exceptions.BusTrackerException

val mapExceptionsF: (Throwable) -> BusTrackerException = { it ->
    if (it is BusTrackerException) it
    else BusTrackerException.SoapError(it.message)
}