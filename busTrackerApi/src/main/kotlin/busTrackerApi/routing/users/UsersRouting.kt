package busTrackerApi.routing.users

import arrow.core.getOrElse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get

fun Route.usersRouting() = route("/users") {
    val userRepo by inject<CoroutineDatabase>()

    post("/register") {
        val user = call.receiveText().deserialized()
        userRepo.getCollection<User>("Users").insertOne(
            User(
                user["username"].asString().getOrElse { throw Exception("Username not found") },
                user["password"].asString().getOrElse { throw Exception("Password not found") },
                user["email"].asString().getOrElse { throw Exception("Email not found") }
            )
        )
        call.respond(HttpStatusCode.Created)
    }

    post("/verify") {

    }

    post("/login") {
        TODO()
    }
}