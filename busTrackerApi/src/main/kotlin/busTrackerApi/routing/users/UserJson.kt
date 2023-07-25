package busTrackerApi.routing.users

import arrow.core.continuations.either
import busTrackerApi.extensions.hashAsString
import busTrackerApi.extensions.toBusTrackerException
import com.toxicbakery.bcrypt.Bcrypt
import simpleJson.JsonNode
import simpleJson.asString
import simpleJson.get

suspend fun createUser(json : JsonNode) = either {
    User(
        username = json["username"]
            .asString().toBusTrackerException().bind()
            .validateUsername().bind(),
        password = json["password"]
            .asString().toBusTrackerException().bind()
            .validatePassword().bind()
            .let(Bcrypt::hashAsString),
        email = json["email"]
            .asString().toBusTrackerException().bind()
            .validateMail().bind(),
        verified = false
    )
}