package busTrackerApi.routing.users

import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class User(
    val _id: Id<User> = newId(),
    val username: String,
    val password: String,
    val email: String,
    val verified: Boolean
)