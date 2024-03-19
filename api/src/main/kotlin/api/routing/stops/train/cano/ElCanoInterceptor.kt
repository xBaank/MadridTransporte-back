package api.routing.stops.train.cano

import api.utils.getUnsafeOkHttpClientBuilder
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

val canoHttpClient = getUnsafeOkHttpClientBuilder()
    .callTimeout(60.seconds.toJavaDuration())
    .connectTimeout(60.seconds.toJavaDuration())
    .readTimeout(60.seconds.toJavaDuration())
    .addInterceptor(AuthHeaderInterceptor())
    .build()

class AuthHeaderInterceptor : Interceptor {
    private val id: String = "718da3df4199ede4"

    override fun intercept(chain: Chain): Response {
        val map: Map<String, String?>?
        val request: Request = chain.request()
        val path = ElcanoClientAuth.Builder("and20210615", "Jthjtr946RTt")
            .host(request.url.host).contentType("application/json;charset=utf-8")
            .path(request.url.encodedPath)
        var encodedQuery = request.url.encodedQuery
        if (encodedQuery == null) {
            encodedQuery = ""
        }
        val httpMethodName = path.params(encodedQuery).xElcanoClient("AndroidElcanoApp").xElcanoUserId(this.id)
            .httpMethodName(request.method)
        val buffer = Buffer()
        val body = request.body
        body?.writeTo(buffer)
        val build =
            httpMethodName.payload(buffer.readUtf8().replace(" ", "", false)).build()
        map = try {
            build.getHeaders()
        } catch (e: Exception) {
            null
        }
        val newBuilder: Request.Builder = chain.request().newBuilder()
        val newBuilder2: Headers.Builder = chain.request().headers.newBuilder()
        if (map != null) {
            for ((key, value) in map) {
                if (value == null) continue
                newBuilder2.addUnsafeNonAscii(key, value)
            }
        }
        return chain.proceed(newBuilder.headers(newBuilder2.build()).build())
    }
}