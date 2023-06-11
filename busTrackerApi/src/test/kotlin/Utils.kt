import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import simpleJson.jObject
import simpleJson.serialized


suspend fun ApplicationTestBuilder.register(mail: String, username: String, password: String) =
    client.post("/v1/users/register?redirectUrl=http://localhost:8080/v1/users/verify") {
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "email" += mail
            "username" += username
            "password" += password
        }.serialized())
    }

suspend fun ApplicationTestBuilder.login(mail: String, password: String) =
    client.post("/v1/users/login") {
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "email" += mail
            "password" += password
        }.serialized())
    }

suspend fun ApplicationTestBuilder.verify(token: String) =
    client.get("/v1/users/verify?token=$token")

fun initEnv() {
    //Even if it not used
    System.setProperty("MONGO_CONNECTION_STRING", "")
    System.setProperty("MONGO_DATABASE_NAME", "")
}