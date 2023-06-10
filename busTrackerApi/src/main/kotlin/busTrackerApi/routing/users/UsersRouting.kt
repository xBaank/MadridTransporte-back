package busTrackerApi.routing.users

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
import busTrackerApi.*
import busTrackerApi.plugins.Signer
import busTrackerApi.plugins.saltRounds
import com.auth0.jwt.JWTVerifier
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.email.EmailBuilder
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get
import java.net.URLEncoder

const val mailValidation = """^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$"""

fun Route.authRouting() {
    val userRepo by inject<CoroutineDatabase>()
    val signer by inject<Signer>()
    val verifier by inject<JWTVerifier>()
    val mailer by inject<Mailer>()

    post("/register") {
        val user = call.receiveText().deserialized()
        val redirectUrl = call.request.queryParameters["redirectUrl"]?.also { URLEncoder.encode(it, "utf-8") }
            ?: badRequest("Missing redirectUrl")

        val userTyped = User(
            username = user["username"].asString()
                .getOrElse { return@post badRequest(it.message) },
            password = user["password"].asString()
                .map { Bcrypt.hashAsString(it, saltRounds) }
                .getOrElse { return@post badRequest(it.message) },
            email = user["email"].asString()
                .validateMail()
                .getOrElse { return@post badRequest(it.message) },
            verified = false
        )

        val userExists = userRepo.getCollection<User>().findOne(User::email eq userTyped.email) != null
        if (userExists) conflict("User already exists")

        userRepo.getCollection<User>().insertOne(userTyped)

        val rawToken = signer { withClaim("email", userTyped.email) }

        val token = URLEncoder.encode(rawToken, "utf-8")

        val email = EmailBuilder.startingBlank()
            .from("BusTracker", "noreply@bustracker.com")
            .to(userTyped.username, userTyped.email)
            .withSubject("Account Verification")
            .withPlainText("Click here to verify your account: ${redirectUrl}/v1/users/verify?token=$token")
            .buildEmail()

        CoroutineScope(Dispatchers.IO).launch { mailer.sendMail(email) }

        call.respond(HttpStatusCode.Created)
    }

    get("/verify") {
        val rawToken = call.request.queryParameters["token"] ?: return@get badRequest("Token not found")
        val token = Either.catch { verifier.verify(rawToken) }.getOrElse { return@get unauthorized("Invalid token") }
        val email = token.getClaim("email").asString() ?: return@get badRequest("Email not found")

        val user = userRepo.getCollection<User>()
            .findOne(User::email eq email)
            ?: return@get badRequest("User not found")

        if (user.verified) return@get badRequest("User already verified")

        userRepo.getCollection<User>().updateOne(user.copy(verified = true))

        call.respond(HttpStatusCode.OK)
    }

    post("/login") {
        val user = call.receiveText().deserialized()
        val email = user["email"].asString().getOrElse { return@post badRequest(it.message) }
        val password = user["password"].asString().getOrElse { return@post badRequest(it.message) }

        val userTyped =
            userRepo.getCollection<User>().findOne(User::email eq email) ?: return@post notFound("User not found")

        if (!Bcrypt.verifyHash(password, userTyped.password)) unauthorized("Wrong password")
        if (!userTyped.verified) badRequest("User not verified")

        val rawToken = signer { withClaim("email", userTyped.email) }

        val token = URLEncoder.encode(rawToken, "utf-8")
        val tokenObject = accessTokenObject(token)

        call.respondText(tokenObject, ContentType.Application.Json, HttpStatusCode.OK)
    }
}


fun Either<Exception, String>.validateMail(): Either<Exception, String> = flatMap {
    if (!it.matches(mailValidation.toRegex()))
        Exception("Invalid mail").left() else this
}