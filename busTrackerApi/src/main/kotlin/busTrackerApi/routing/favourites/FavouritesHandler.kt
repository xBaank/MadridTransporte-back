package busTrackerApi.routing.favourites

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.inject
import busTrackerApi.extensions.toBusTrackerException
import busTrackerApi.routing.Response.ResponseJson
import busTrackerApi.routing.Response.ResponseRaw
import busTrackerApi.routing.users.User
import busTrackerApi.utils.Call
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import simpleJson.asJson
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get

val db by inject<CoroutineDatabase>()
suspend fun Call.createFavourite() = either {
    val stopToSave = call.receiveText().deserialized()
    val stopType = stopToSave["stopType"].asString().toBusTrackerException().bind()
    val stopId = stopToSave["stopId"].asString().toBusTrackerException().bind()
    val name = stopToSave["name"].asString().toBusTrackerException().bind()
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    val user: User = db.getCollection<User>().findOne(User::email eq email) ?: shift(NotFound("Email not found"))

    db.getCollection<Favourite>().insertOne(
        Favourite(
            email = user.email,
            stopType = stopType,
            stopId = stopId,
            name = name
        )
    )

    ResponseRaw(HttpStatusCode.Created)
}

suspend fun Call.getFavourites() = either {
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    db.getCollection<User>().findOne(User::email eq email) ?: shift(NotFound("Email not found"))
    val favourites = db.getCollection<Favourite>().find(Favourite::email eq email).toList()

    ResponseJson(favourites.map(Favourite::toJson).asJson(), HttpStatusCode.OK)
}

suspend fun Call.getFavourite() = either {
    val id = call.parameters.getWrapped("id").bind()
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    val filters = arrayOf(Favourite::stopId eq id, Favourite::email eq email)
    db.getCollection<User>().findOne(User::email eq email) ?: shift(NotFound("Email not found"))
    val favourite: Favourite = db.getCollection<Favourite>().findOne(*filters) ?: shift(NotFound("Favourite not found"))

    ResponseJson(favourite.toJson(), HttpStatusCode.OK)
}

suspend fun Call.deleteFavourite() = either {
    val id = call.parameters.getWrapped("id").bind()
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    db.getCollection<Favourite>().deleteOne(Favourite::stopId eq id, Favourite::email eq email)

    ResponseRaw(HttpStatusCode.NoContent)
}