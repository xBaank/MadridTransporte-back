package busTrackerApi.routing.users

import arrow.core.getOrElse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import simpleJson.deserialized
import simpleJson.get

fun Route.usersRouting() = route("/users") {
    val userRepo by inject<CoroutineDatabase>()

    post("/register") {
        val user = call.receiveText().deserialized()
        userRepo.getCollection<Any>("Users").insertOne(object {
            val username = user["username"].getOrElse { throw Exception("Username not found") }
            val password = user["password"].getOrElse { throw Exception("Password not found") }
        })
        call.respond(HttpStatusCode.Created)
    }

    post("/verify") {

    }

    post("/login") {
        TODO()
    }
}