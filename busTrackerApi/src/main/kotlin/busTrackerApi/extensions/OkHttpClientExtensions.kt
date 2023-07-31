package busTrackerApi.extensions

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import simpleJson.JsonNode
import simpleJson.serialized

fun OkHttpClient.get(url: String) = newCall(Request.Builder()
    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:64.0) Gecko/20100101 Firefox/64.0")
    .url(url)
    .get()
    .build()
)

fun OkHttpClient.post(url: String, body: JsonNode) =
    newCall(Request.Builder().url(url).post(body.serialized().toRequestBody("application/json".toMediaType())).build())