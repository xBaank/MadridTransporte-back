package busTrackerApi

import com.toxicbakery.bcrypt.Bcrypt
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import simpleJson.jObject
import simpleJson.serialized

fun Dotenv.getenvOrThrow(key: String): String =
    get(key) ?: throw IllegalStateException("Environment variable $key is not set")

fun Bcrypt.hashAsString(input: String, saltRounds: Int = 10): String =
    hash(input, saltRounds).toString(Charsets.UTF_8)

fun Bcrypt.verifyHash(input: String, hash: String): Boolean =
    verify(input, hash.toByteArray(Charsets.UTF_8))

suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(message: String?): Nothing {
    val messageResponse = message ?: "Bad Request"
    val messageResponseJson = jObject {
        "message" to messageResponse
    }.serialized()
    call.respondText(messageResponseJson, ContentType.Application.Json, HttpStatusCode.BadRequest)
    error(messageResponse) //This should not return
}