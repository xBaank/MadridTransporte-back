package busTrackerApi.extensions

import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.routing.Response
import busTrackerApi.utils.errorObject
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import simpleJson.serialized

suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(message: String?) {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

suspend fun PipelineContext<Unit, ApplicationCall>.unauthorized(ex: BusTrackerException.Unauthorized) {
    val messageResponse = ex.message ?: "Unauthorized"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Unauthorized)
}

suspend fun PipelineContext<Unit, ApplicationCall>.notFound(ex: BusTrackerException.NotFound) {
    val messageResponse = ex.message ?: "Not found"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.NotFound)
}

suspend fun PipelineContext<Unit, ApplicationCall>.conflict(message: String?) {
    val messageResponse = message ?: "Conflict"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Conflict)
}

suspend fun PipelineContext<Unit, ApplicationCall>.soapError(ex: BusTrackerException.SoapError) {
    val messageResponse = ex.message ?: "Soap Error"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

suspend fun PipelineContext<Unit, ApplicationCall>.internalServerError(ex: BusTrackerException.InternalServerError) {
    val messageResponse = ex.message ?: "Internal Server Error"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.InternalServerError)
}

suspend fun PipelineContext<Unit, ApplicationCall>.handleError(ex: BusTrackerException) = when (ex) {
    is BusTrackerException.NotFound -> notFound(ex)
    is BusTrackerException.SoapError -> soapError(ex)
    is BusTrackerException.Unauthorized -> unauthorized(ex)
    is BusTrackerException.InternalServerError -> internalServerError(ex)
    is BusTrackerException.JsonError -> badRequest(ex.message)
    is BusTrackerException.QueryParamError -> badRequest(ex.message)
    is BusTrackerException.ValidationException -> badRequest(ex.message)
    is BusTrackerException.BadRequest -> badRequest(ex.message)
}




suspend fun PipelineContext<Unit, ApplicationCall>.handleResponse(response: Response): Unit = when (response) {
    is Response.ResponseJson -> call.respondText(response.json.serialized(), ContentType.Application.Json)
    is Response.ResponseRaw -> call.respond(response.status)
    is Response.ResponseRedirect -> call.respondRedirect(response.url)
}


