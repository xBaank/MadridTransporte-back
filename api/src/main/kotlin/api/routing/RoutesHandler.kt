package api.routing

import api.exceptions.BusTrackerException
import api.extensions.serializedMemo
import api.utils.Pipeline
import api.utils.errorObject
import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.withIndex
import simpleJson.serialized

private val logger = KtorSimpleLogger("ResponseLogger")


suspend fun Pipeline.badRequest(ex: BusTrackerException) {
    val messageResponse = ex.message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

private suspend fun Pipeline.unauthorized(ex: BusTrackerException.Unauthorized) {
    val messageResponse = ex.message ?: "Unauthorized"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Unauthorized)
}

private suspend fun Pipeline.notFound(ex: BusTrackerException.NotFound) {
    val messageResponse = ex.message ?: "Not found"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.NotFound)
}

private suspend fun Pipeline.conflict(ex: BusTrackerException.Conflict) {
    val messageResponse = ex.message ?: "Conflict"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Conflict)
}

private suspend fun Pipeline.tooManyRequests(ex: BusTrackerException.TooManyRequests) {
    val messageResponse = ex.message ?: "Too many requests"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.TooManyRequests)
}

private suspend fun Pipeline.soapError(ex: BusTrackerException.SoapError) {
    val messageResponse = ex.message ?: "Soap Error"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

private suspend fun Pipeline.internalServerError(ex: BusTrackerException.InternalServerError) {
    val messageResponse = ex.message ?: "Internal Server Error"
    val messageResponseJson = errorObject(messageResponse).serialized()

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.InternalServerError)
}

suspend fun Pipeline.handleError(ex: BusTrackerException) = when (ex) {
    is BusTrackerException.NotFound -> notFound(ex)
    is BusTrackerException.SoapError -> soapError(ex.also(logger::error))
    is BusTrackerException.Unauthorized -> unauthorized(ex)
    is BusTrackerException.InternalServerError -> internalServerError(ex.also(logger::error))
    is BusTrackerException.JsonError -> badRequest(ex.also(logger::error))
    is BusTrackerException.QueryParamError -> badRequest(ex)
    is BusTrackerException.ValidationException -> badRequest(ex)
    is BusTrackerException.BadRequest -> badRequest(ex)
    is BusTrackerException.Conflict -> conflict(ex)
    is BusTrackerException.TooManyRequests -> tooManyRequests(ex)
}

suspend fun Pipeline.handleResponse(response: Response): Unit = when (response) {
    is Response.ResponseJson -> call.respondText(
        response.json.serialized(),
        ContentType.Application.Json,
        response.status
    )

    is Response.ResponseJsonCached -> call.respondText(
        response.json.serializedMemo(),
        ContentType.Application.Json,
        response.status
    )

    is Response.ResponseRaw -> call.respond(response.status)

    is Response.ResponseFlowJson -> {
        call.respondBytesWriter(status = response.status, contentType = ContentType.Application.Json) {
            writeStringUtf8("[")
            response.json.withIndex().collect {
                if (it.index == 0) {
                    writeStringUtf8(it.value.serialized())
                    return@collect
                }
                writeStringUtf8(",")
                writeStringUtf8(it.value.serialized())
            }
            writeStringUtf8("]")
        }
    }

    is Response.ResponseString -> call.respondText(
        response.data,
        contentType = response.contentType,
        status = response.status
    )
}

suspend inline fun Pipeline.handle(f: () -> Either<BusTrackerException, Response>) =
    f().fold({ handleError(it) }, { handleResponse(it) })