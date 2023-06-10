package busTrackerApi

import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import simpleJson.jObject
import simpleJson.serialized

fun getenvOrThrow(key: String): String =
    System.getProperty(key) ?: throw IllegalStateException("Environment variable $key is not set")


fun Bcrypt.hashAsString(input: String, saltRounds: Int = 10): String =
    hash(input, saltRounds).toString(Charsets.UTF_8)

fun Bcrypt.verifyHash(input: String, hash: String): Boolean =
    verify(input, hash.toByteArray(Charsets.UTF_8))

suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(message: String?) {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse)

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
}

suspend fun PipelineContext<Unit, ApplicationCall>.unauthorized(message: String?) {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse)

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Unauthorized)
}

suspend fun PipelineContext<Unit, ApplicationCall>.notFound(message: String?) {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse)

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.NotFound)
}

suspend fun PipelineContext<Unit, ApplicationCall>.conflict(message: String?) {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = errorObject(messageResponse)

    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.Conflict)
}

fun errorObject(message: String): String = jObject {
    "message" += message
}.serialized()

fun accessTokenObject(token: String) = jObject {
    "token" += token
}.serialized()