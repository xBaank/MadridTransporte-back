package busTrackerApi.extensions

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import simpleJson.JsonNode
import simpleJson.serialized

fun OkHttpClient.get(url: String, headers : Map<String,String> = mapOf()) = newCall(Request.Builder()
    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:64.0) Gecko/20100101 Firefox/64.0")
    .apply { headers.forEach { (k,v) -> header(k,v) } }
    .url(url)
    .get()
    .build()
)

fun OkHttpClient.post(url: String, body: JsonNode, headers: Map<String, String> = mapOf(), ) = newCall(Request.Builder()
    .url(url)
    .apply { headers.forEach { (k,v) -> header(k,v) } }
    .post(body.serialized().toRequestBody("application/json".toMediaType()))
    .build()
)