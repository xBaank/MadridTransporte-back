import io.github.serpro69.kfaker.faker
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import simpleJson.jObject
import simpleJson.serialized
import java.net.URLEncoder


suspend fun ApplicationTestBuilder.register(mail: String, username: String, password: String): HttpResponse {
    val redirectUrl = URLEncoder.encode("http://localhost:8080/v1/users/verify", "UTF-8")
    val backUrl = URLEncoder.encode("http://localhost:8080", "UTF-8")
    return client.post("/v1/users/register?redirectUrl=$redirectUrl&backUrl=$backUrl") {
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "email" += mail
            "username" += username
            "password" += password
        }.serialized())
    }
}

suspend fun ApplicationTestBuilder.login(mail: String, password: String) =
    client.post("/v1/users/login") {
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "email" += mail
            "password" += password
        }.serialized())
    }

suspend fun ApplicationTestBuilder.sendResetPassword(mail: String) =
    client.post("/v1/users/send-reset-password?redirectUrl=/ping") {
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "email" += mail
        }.serialized())
    }

suspend fun ApplicationTestBuilder.resetPassword(token: String, password: String) =
    client.put("/v1/users/reset-password?token=$token") {
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "password" += password
        }.serialized())
    }

suspend fun ApplicationTestBuilder.verify(token: String) =
    client.get("/v1/users/verify?token=$token&redirectUrl=/ping&backUrl=http://localhost:8080")

suspend fun ApplicationTestBuilder.getFavourites(token: String) =
    client.get("/v1/favorites") {
        header("Authorization", "Bearer $token")
    }

suspend fun ApplicationTestBuilder.addFavourite(token: String, stopType: String, stopId: String) =
    client.post("/v1/favorites") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(jObject {
            "stopType" += stopType
            "stopId" += stopId
        }.serialized())
    }

suspend fun ApplicationTestBuilder.deleteFavourite(token: String, stopId: String) =
    client.delete("/v1/favorites/$stopId") {
        header("Authorization", "Bearer $token")
    }

suspend fun ApplicationTestBuilder.getFavourite(token: String, stopId: String) =
    client.get("/v1/favorites/$stopId") {
        header("Authorization", "Bearer $token")
    }

fun getFakerUserData(): Triple<String, String, String> {
    val faker = faker { }
    val mail = faker.internet.safeEmail()
    val username = faker.name.name()
    val password = faker.crypto.md5()
    return Triple(mail, username, password)
}