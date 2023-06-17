package busTrackerApi.routing.users

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class User(
    val _id: Id<User> = newId(),
    val username: String,
    val password: String,
    val email: String,
    val verified: Boolean
)

const val mailValidation = """^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$"""

fun Either<Exception, String>.validateMail(): Either<Exception, String> = flatMap {
    if (!it.matches(mailValidation.toRegex()))
        Exception("Invalid mail").left() else this
}

fun Either<Exception, String>.validateUsername(): Either<Exception, String> = flatMap {
    if (it.length < 3) Exception("Username too short").left() else it.right()
}

fun Either<Exception, String>.validatePassword(): Either<Exception, String> = flatMap {
    if (it.length < 8) Exception("Password too short").left() else it.right()
}