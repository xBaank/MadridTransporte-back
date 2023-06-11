package busTrackerApi.routing.users

import arrow.core.*
import busTrackerApi.*
import busTrackerApi.config.Signer
import busTrackerApi.config.saltRounds
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
            ?: return@post badRequest("Missing redirectUrl")

        val userTyped = User(
            username = user["username"].asString()
                .validateUsername()
                .getOrElse { return@post badRequest(it.message) },
            password = user["password"].asString()
                .validatePassword()
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
        val redirectUrl = call.request.queryParameters["redirectUrl"] ?: return@get badRequest("Missing redirectUrl")

        val rawToken = call.request.queryParameters["token"] ?: return@get badRequest("Token not found")
        val token = Either.catch { verifier.verify(rawToken) }.getOrElse { return@get unauthorized("Invalid token") }
        val email = token.getClaim("email").asString() ?: return@get badRequest("Email not found")

        val user = userRepo.getCollection<User>()
            .findOne(User::email eq email)
            ?: return@get badRequest("User not found")

        if (user.verified) return@get badRequest("User already verified")

        userRepo.getCollection<User>().updateOne(user.copy(verified = true))

        call.respondRedirect(redirectUrl)
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

    post("/send-reset-password") {
        val user = call.receiveText().deserialized()
        val email = user["email"].asString().getOrElse { return@post badRequest(it.message) }
        val redirectUrl = call.request.queryParameters["redirectUrl"]?.also { URLEncoder.encode(it, "utf-8") }
            ?: return@post badRequest("Missing redirectUrl")

        val userTyped =
            userRepo.getCollection<User>().findOne(User::email eq email) ?: return@post notFound("User not found")

        val rawToken = signer { withClaim("email", userTyped.email) }

        val token = URLEncoder.encode(rawToken, "utf-8")

        val emailToSend = EmailBuilder.startingBlank()
            .from("BusTracker", "noreply@bustracker.com")
            .to(userTyped.username, userTyped.email)
            .withSubject("Reset Password")
            .withPlainText("Click here to verify your account: ${redirectUrl}/v1/users/verify?token=$token")
            .buildEmail()

        CoroutineScope(Dispatchers.IO).launch { mailer.sendMail(emailToSend) }

        call.respond(HttpStatusCode.OK)
    }

    put("/reset-password") {
        val token = call.request.queryParameters["token"] ?: return@put badRequest("Token not found")
        val rawToken = Either.catch { verifier.verify(token) }.getOrElse { return@put unauthorized("Invalid token") }
        val email = rawToken.getClaim("email").asString() ?: return@put badRequest("Email not found")

        val newPass =
            call.receiveText().deserialized()["password"].asString().getOrElse { return@put badRequest(it.message) }

        val userTyped =
            userRepo.getCollection<User>().findOne(User::email eq email) ?: return@put notFound("User not found")

        val user = userTyped.copy(password = Bcrypt.hashAsString(newPass, saltRounds))
        userRepo.getCollection<User>().updateOne(user)

        call.respond(HttpStatusCode.OK)
    }
}


fun Either<Exception, String>.validateMail(): Either<Exception, String> = flatMap {
    if (!it.matches(mailValidation.toRegex()))
        Exception("Invalid mail").left() else this
}

fun Either<Exception, String>.validateUsername(): Either<Exception, String> = flatMap {
    if (it.length < 3) Exception("Username too short").left() else it.right()
}

fun Either<Exception, String>.validatePassword(): Either<Exception, String> = flatMap {
    if (it.length < 8) Exception("Password too short").left() else it.right()
}