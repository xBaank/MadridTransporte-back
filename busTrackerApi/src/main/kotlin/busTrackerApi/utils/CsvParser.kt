package busTrackerApi.utils

import busTrackerApi.extensions.asJson
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import simpleJson.JsonArray
import simpleJson.asJson

//Use json syntax to simplify its usage
fun csvToJson(source: String): JsonArray {
    //TODO There is a bug here
    val rows: List<Map<String, String>> = csvReader().readAllWithHeader(source)
    return rows.map { it.asJson() }.asJson()
}