package busTrackerApi.routing.favorites

import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class Favorite(
    val _id: Id<Favorite> = newId(),
    val email: String,
    val stopType: String,
    val stopId: String,
    val name: String
)

