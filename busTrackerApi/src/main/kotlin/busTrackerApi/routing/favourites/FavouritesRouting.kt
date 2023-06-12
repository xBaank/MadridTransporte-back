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

fun Route.favoritesRouting() = authenticate("user") {
    val db by inject<CoroutineDatabase>()

    post {
        val stopToSave = call.receiveText().deserialized()
        val stopType = stopToSave["stopType"].asString().getOrElse { return@post badRequest(it.message) }
        val stopId = stopToSave["stopId"].asString().getOrElse { return@post badRequest(it.message) }
        val name = stopToSave["name"].asString().getOrElse { "Default" }

        val email =
            call.principal<JWTPrincipal>()?.get("email") ?: return@post badRequest("Missing email in token")
        val user =
            db.getCollection<User>().findOne(User::email eq email) ?: return@post badRequest("Email not found")
        db.getCollection<Favourite>().insertOne(
            Favourite(
                email = user.email,
                stopType = stopType,
                stopId = stopId,
                name = name
            )
        )

        call.respond(HttpStatusCode.Created)
    }

    get {
        val email =
            call.principal<JWTPrincipal>()?.get("email") ?: return@get badRequest("Missing email in token")
        db.getCollection<User>().findOne(User::email eq email) ?: return@get badRequest("Email not found")
        val favourites = db.getCollection<Favourite>().find(Favourite::email eq email).toList()
        val respondObject = favourites.map(Favourite::toJson).asJson()
        call.respondText(respondObject.serialized(), ContentType.Application.Json)
    }

    get("/{id}") {
        val id = call.parameters["id"] ?: return@get badRequest("Missing id")
        val email =
            call.principal<JWTPrincipal>()?.get("email") ?: return@get badRequest("Missing email in token")
        db.getCollection<User>().findOne(User::email eq email) ?: return@get badRequest("Email not found")
        val favourite =
            db.getCollection<Favourite>().findOne(Favourite::stopId eq id)
                ?: return@get badRequest("Favourite not found")
        val respondObject = favourite.toJson().asJson()
        call.respondText(respondObject.serialized(), ContentType.Application.Json)
    }

    delete("/{id}") {
        val id = call.parameters["id"] ?: return@delete badRequest("Missing id")
        val email =
            call.principal<JWTPrincipal>()?.get("email") ?: return@delete badRequest("Missing email in token")
        db.getCollection<User>().findOne(User::email eq email) ?: return@delete badRequest("Email not found")
        db.getCollection<Favourite>().deleteOne(Favourite::stopId eq id)
        call.respond(HttpStatusCode.NoContent)
    }
}

