package api.routing

import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import simpleJson.JsonNode

sealed interface Response {
    val status: HttpStatusCode

    data class ResponseFlowJson(val json: Flow<JsonNode>, override val status: HttpStatusCode) : Response
    data class ResponseJson(val json: JsonNode, override val status: HttpStatusCode) : Response
    data class ResponseJsonCached(val json: JsonNode, override val status: HttpStatusCode) : Response
    data class ResponseRaw(override val status: HttpStatusCode) : Response
    data class ResponseString(val data: String, val contentType: ContentType, override val status: HttpStatusCode) :
        Response
}