package busTrackerApi.extensions

import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.CloseSocketException
import busTrackerApi.routing.Response
import busTrackerApi.utils.Call
import busTrackerApi.utils.errorObject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.websocket.*
import simpleJson.serialized

suspend fun Call.badRequest(message: String?) {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

private suspend fun Call.unauthorized(ex: BusTrackerException.Unauthorized) {
    val messageResponse = ex.message ?: "Unauthorized"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Unauthorized)
}

private suspend fun Call.notFound(ex: BusTrackerException.NotFound) {
    val messageResponse = ex.message ?: "Not found"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.NotFound)
}

private suspend fun Call.conflict(ex: BusTrackerException.Conflict) {
    val messageResponse = ex.message ?: "Conflict"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Conflict)
}

private suspend fun Call.soapError(ex: BusTrackerException.SoapError) {
    val messageResponse = ex.message ?: "Soap Error"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

private suspend fun Call.internalServerError(ex: BusTrackerException.InternalServerError) {
    val messageResponse = ex.message ?: "Internal Server Error"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.InternalServerError)
}

suspend fun Call.handleError(ex: BusTrackerException) = when (ex) {
    is BusTrackerException.NotFound -> notFound(ex)
    is BusTrackerException.SoapError -> soapError(ex)
    is BusTrackerException.Unauthorized -> unauthorized(ex)
    is BusTrackerException.InternalServerError -> internalServerError(ex)
    is BusTrackerException.JsonError -> badRequest(ex.message)
    is BusTrackerException.QueryParamError -> badRequest(ex.message)
    is BusTrackerException.ValidationException -> badRequest(ex.message)
    is BusTrackerException.BadRequest -> badRequest(ex.message)
    is BusTrackerException.Conflict -> conflict(ex)
}

suspend fun DefaultWebSocketSession.handleError(ex: CloseSocketException) =
    close(CloseReason(ex.closeReason, ex.message ?: "Close Socket"))

suspend fun Call.handleResponse(response: Response): Unit = when (response) {
    is Response.ResponseJson -> call.respondText(response.json.serialized(), ContentType.Application.Json)
    is Response.ResponseRaw -> call.respond(response.status)
    is Response.ResponseRedirect -> call.respondRedirect(response.url)
}

