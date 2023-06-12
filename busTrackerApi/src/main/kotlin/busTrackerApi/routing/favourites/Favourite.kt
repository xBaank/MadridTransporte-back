package busTrackerApi.routing.favourites

import org.litote.kmongo.Id
import org.litote.kmongo.newId
import simpleJson.jObject

data class Favourite(
    val _id: Id<Favourite> = newId(),
    val email: String,
    val stopType: String,
    val stopId: String,
    val name: String
)

fun Favourite.toJson() = jObject {
    "stopType" += stopType
    "stopId" += stopId
    "email" += email
}