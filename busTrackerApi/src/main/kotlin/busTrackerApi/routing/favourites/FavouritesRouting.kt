package busTrackerApi.routing.favourites

import arrow.core.getOrElse
import busTrackerApi.badRequest
import busTrackerApi.routing.users.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import simpleJson.*

fun Route.favouritesRouting() = authenticate("user") {
    val db by inject<CoroutineDatabase>()

    post {
        val stopToSave = call.receiveText().deserialized()
        val stopType = stopToSave["stopType"].asString().getOrElse { return@post badRequest(it.message) }
        val stopId = stopToSave["stopId"].asString().getOrElse { return@post badRequest(it.message) }

        val username =
            call.principal<JWTPrincipal>()?.get("username") ?: return@post badRequest("Missing username in token")
        val user =
            db.getCollection<User>().findOne(User::username eq username) ?: return@post badRequest("User not found")
        db.getCollection<Favourite>().insertOne(
            Favourite(
                username = user.username,
                stopType = stopType,
                stopId = stopId
            )
        )

        call.respond(HttpStatusCode.Created)
    }

    get {
        val username =
            call.principal<JWTPrincipal>()?.get("username") ?: return@get badRequest("Missing username in token")
        db.getCollection<User>().findOne(User::username eq username) ?: return@get badRequest("User not found")
        val favourites = db.getCollection<Favourite>().find(Favourite::username eq username).toList()
        val respondObject = favourites.map(Favourite::toJson).asJson()
        call.respondText(respondObject.serialized(), ContentType.Application.Json)
    }
}

