package busTrackerApi.routing.abono

import arrow.core.Either
import arrow.core.left
import simpleJson.JsonNode
import simpleJson.jObject

fun isFound(value: Int) = value == 1
fun buildJson(data: SS_prepagoConsultaSaldo): Either<Exception, JsonNode> {
    if (data.ttpSearchResult?.value?.toIntOrNull()?.let(::isFound) == false) return Exception("Not found").left()
    val json = jObject {
        data.ttpSearchResult.ttpData.TTPNumber
    }
}