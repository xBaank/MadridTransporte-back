package busTrackerApi.routing

import io.ktor.http.*
import simpleJson.JsonNode

sealed interface Response {
    val status: HttpStatusCode

    data class ResponseJson(val json: JsonNode, override val status: HttpStatusCode) : Response
    data class ResponseRaw(override val status: HttpStatusCode) : Response
    data class ResponseRedirect(val url: String, override val status: HttpStatusCode = HttpStatusCode.PermanentRedirect) : Response
}