package busTrackerApi

import com.auth0.jwt.JWTCreator
import com.toxicbakery.bcrypt.Bcrypt
import io.github.reactivecircus.cache4k.Cache
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import simpleJson.jObject
import simpleJson.serialized
import java.util.*
import kotlin.time.Duration

inline fun <T : Any, K : Any> Cache<T, K>.getOrPut(key: T, value: () -> K): K =
    get(key) ?: value().also { put(key, it) }

fun getenvOrThrow(key: String): String =
    System.getenv(key) ?: System.getProperty(key) ?: throw IllegalStateException("Environment variable $key is not set")

fun getenvOrNull(key: String): String? =
    System.getenv(key) ?: System.getProperty(key) ?: null

fun JWTCreator.Builder.withExpiresIn(duration: Duration): JWTCreator.Builder =
    withExpiresAt(Date(System.currentTimeMillis() + duration.inWholeMilliseconds))

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