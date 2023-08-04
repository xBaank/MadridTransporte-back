package busTrackerApi.routing.stops.emt

import arrow.core.continuations.either
import simpleJson.JsonNode
import simpleJson.asInt
import simpleJson.asString
import simpleJson.get

data class LoginResponse(
    val accessToken: String,
    val tokenSecExpiration: Int,
    val apiCounter: ApiCounter
)

data class ApiCounter(
    val current: Int,
    val max: Int,
    val left : Int = max - current
)


suspend fun parseLoginResponse(json: JsonNode) = either {
    LoginResponse(
        accessToken = json["data"]["accessToken"].asString().bind(),
        tokenSecExpiration = json["data"]["tokenSecExpiration"].asInt().bind(),
        apiCounter = ApiCounter(
            current = json["data"]["apiCounter"]["current"].asInt().bind(),
            max = json["data"]["apiCounter"]["dailyUse"].asInt().bind()
        )
    )
}