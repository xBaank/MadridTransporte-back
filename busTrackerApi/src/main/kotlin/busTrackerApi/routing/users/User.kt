package busTrackerApi.routing.users

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.hashAsString
import busTrackerApi.extensions.toBusTrackerException
import com.toxicbakery.bcrypt.Bcrypt
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import simpleJson.JsonNode
import simpleJson.asString
import simpleJson.get

const val mailValidation = """^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$"""

data class User(
    val _id: Id<User> = newId(),
    val username: String,
    val password: String,
    val email: String,
    val verified: Boolean
)

suspend fun createUser(json : JsonNode) = either {
    User(
        username = json["username"]
            .asString().toBusTrackerException().bind()
            .validateUsername().bind(),
        password = json["password"]
            .asString().toBusTrackerException().bind()
            .validatePassword().bind()
            .let(Bcrypt::hashAsString),
        email = json["email"]
            .asString().toBusTrackerException().bind()
            .validateMail().bind(),
        verified = false
    )
}

fun String.validateMail(): Either<BusTrackerException, String> =
    if (!matches(mailValidation.toRegex())) BusTrackerException.ValidationException("Invalid mail").left() else right()

fun String.validateUsername(): Either<BusTrackerException, String> =
    if (length < 3) BusTrackerException.ValidationException("Username too short").left() else right()

fun String.validatePassword(): Either<BusTrackerException, String> =
    if (length < 8) BusTrackerException.ValidationException("Password too short").left() else right()