package busTrackerApi.routing.favorites

import org.litote.kmongo.Id
import org.litote.kmongo.newId
import simpleJson.jObject

data class Favorite(
    val _id: Id<Favorite> = newId(),
    val email: String,
    val stopType: String,
    val stopId: String,
    val name: String
)

fun buildFavoriteJson(favorite : Favorite) = jObject {
    "stopType" += favorite.stopType
    "stopId" += favorite.stopId
    "name" += favorite.name
    "email" += favorite.email
}