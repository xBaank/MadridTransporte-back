package api.routing.stops.emt

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
    val left: Int = max - current
)


suspend fun parseLoginResponse(json: JsonNode) = either {
    LoginResponse(
        accessToken = json["data"][0]["accessToken"].asString().bind(),
        tokenSecExpiration = json["data"][0]["tokenSecExpiration"].asInt().bind(),
        apiCounter = ApiCounter(
            current = json["data"][0]["apiCounter"]["current"].asInt().bind(),
            max = json["data"][0]["apiCounter"]["dailyUse"].asInt().bind()
        )
    )
}