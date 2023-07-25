package busTrackerApi.routing.users

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException
import org.litote.kmongo.Id
import org.litote.kmongo.newId

const val mailValidation = """^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$"""

data class User(
    val _id: Id<User> = newId(),
    val username: String,
    val password: String,
    val email: String,
    val verified: Boolean
)

fun String.validateMail(): Either<BusTrackerException, String> =
    if (!matches(mailValidation.toRegex())) BusTrackerException.ValidationException("Invalid mail").left() else right()

fun String.validateUsername(): Either<BusTrackerException, String> =
    if (length < 3) BusTrackerException.ValidationException("Username too short").left() else right()

fun String.validatePassword(): Either<BusTrackerException, String> =
    if (length < 8) BusTrackerException.ValidationException("Password too short").left() else right()