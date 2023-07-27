package busTrackerApi.routing.favorites

import arrow.core.continuations.either
import busTrackerApi.exceptions.BusTrackerException.NotFound
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.getWrapped
import busTrackerApi.extensions.inject
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

suspend fun Call.createFavorite() = either {
    val stopToSave = call.receiveText().deserialized()
    val stopType = stopToSave["stopType"].asString().bindMap()
    val stopId = stopToSave["stopId"].asString().bindMap()
    val name = stopToSave["name"].asString().bindMap()
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    val user: User = db.getCollection<User>().findOne(User::email eq email) ?: shift(NotFound("Email not found"))

    db.getCollection<Favorite>().insertOne(
        Favorite(
            email = user.email,
            stopType = stopType,
            stopId = stopId,
            name = name
        )
    )

    ResponseRaw(HttpStatusCode.Created)
}

suspend fun Call.getFavorites() = either {
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    db.getCollection<User>().findOne(User::email eq email) ?: shift(NotFound("Email not found"))
    val favourites = db.getCollection<Favorite>().find(Favorite::email eq email).toList()

    ResponseJson(favourites.map(::buildFavoriteJson).asJson(), HttpStatusCode.OK)
}

suspend fun Call.getFavorite() = either {
    val id = call.parameters.getWrapped("id").bind()
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    val filters = arrayOf(Favorite::stopId eq id, Favorite::email eq email)
    db.getCollection<User>().findOne(User::email eq email) ?: shift(NotFound("Email not found"))
    val favourite: Favorite = db.getCollection<Favorite>().findOne(*filters) ?: shift(NotFound("Favourite not found"))

    ResponseJson(buildFavoriteJson(favourite), HttpStatusCode.OK)
}

suspend fun Call.deleteFavorite() = either {
    val id = call.parameters.getWrapped("id").bind()
    val email = call.principal<JWTPrincipal>().getWrapped("email").bind()

    db.getCollection<Favorite>().deleteOne(Favorite::stopId eq id, Favorite::email eq email)

    ResponseRaw(HttpStatusCode.NoContent)
}