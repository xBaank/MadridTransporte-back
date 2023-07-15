package busTrackerApi.exceptions

sealed class BusTrackerException : Exception() {
    class NotFound(override val message: String? = null) : BusTrackerException()
    class SoapError(override val message: String? = null) : BusTrackerException()
    class Unauthorized(override val message: String? = null) : BusTrackerException()
    class QueryParamError(override val message: String? = null) : BusTrackerException()
    class JsonError(override val message: String? = null) : BusTrackerException()
    class ValidationException(override val message: String? = null) : BusTrackerException()
    class InternalServerError(override val message: String? = null) : BusTrackerException()
    class BadRequest(override val message: String? = null) : BusTrackerException()
    class Conflict(override val message: String? = null) : BusTrackerException()
}