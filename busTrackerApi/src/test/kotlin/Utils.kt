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
    client.get("/v1/users/verify?token=$token&redirectUrl=/ping")

suspend fun ApplicationTestBuilder.getFavourites(token: String) =
    client.get("/v1/favorites") {
        header("Authorization", "Bearer ${token}")
    }

suspend fun ApplicationTestBuilder.addFavourite(token: String, stopType: String, stopId: String) =
    client.post("/v1/favorites") {
        header("Authorization", "Bearer ${token}")
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "stopType" += stopType
            "stopId" += stopId
        }.serialized())
    }

fun initEnv() {
    //Even if it not used
    System.setProperty("MONGO_CONNECTION_STRING", "")
    System.setProperty("MONGO_DATABASE_NAME", "")
}