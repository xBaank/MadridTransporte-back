package busTrackerApi.routing.users

import arrow.core.continuations.either
import busTrackerApi.extensions.bindMap
import busTrackerApi.extensions.hashAsString
import com.toxicbakery.bcrypt.Bcrypt
import simpleJson.JsonNode
import simpleJson.asString
import simpleJson.get

suspend fun createUser(json : JsonNode) = either {
    User(
        username = json["username"]
            .asString()
            .bindMap()
            .validateUsername()
            .bind(),
        password = json["password"]
            .asString()
            .bindMap()
            .validatePassword()
            .bind()
            .let(Bcrypt::hashAsString),
        email = json["email"]
            .asString()
            .bindMap()
            .validateMail()
            .bind(),
        verified = false
    )
}