package busTrackerApi.routing.users

import arrow.core.getOrElse
import busTrackerApi.badRequest
import busTrackerApi.hashAsString
import busTrackerApi.plugins.Signer
import busTrackerApi.plugins.saltRounds
import busTrackerApi.verifyHash
import com.auth0.jwt.JWTVerifier
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.email.EmailBuilder
import simpleJson.*
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
                .getOrElse { badRequest(it.message) },
            password = user["password"].asString()
                .map { Bcrypt.hashAsString(it, saltRounds) }
                .getOrElse { badRequest(it.message) },
            email = user["email"].asString()
                .getOrElse { badRequest(it.message) }
                .also { validateMail(it) },
            verified = false
        )

        val userExists = userRepo.getCollection<User>().findOne(User::username eq userTyped.username) != null
        if (userExists) badRequest("User already exists")

        userRepo.getCollection<User>().insertOne(userTyped)

        val rawToken = signer { withClaim("username", userTyped.username) }

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
        val rawToken = call.request.queryParameters["token"] ?: badRequest("Token not found")
        val token = verifier.verify(rawToken)
        val username = token.getClaim("username").asString() ?: badRequest("Username not found")

        val user = userRepo.getCollection<User>()
            .findOne(User::username eq username)
            ?: badRequest("User not found")

        if (user.verified) badRequest("User already verified")

        userRepo.getCollection<User>().updateOne(user.copy(verified = true))

        call.respond(HttpStatusCode.OK)
    }

    post("/login") {
        val user = call.receiveText().deserialized()
        val username = user["username"].asString().getOrElse { badRequest(it.message) }
        val password = user["password"].asString().getOrElse { badRequest(it.message) }

        val userTyped =
            userRepo.getCollection<User>().findOne(User::username eq username) ?: badRequest("User not found")

        if (!Bcrypt.verifyHash(password, userTyped.password)) badRequest("Wrong password")
        if (!userTyped.verified) badRequest("User not verified")

        val rawToken = signer { withClaim("username", userTyped.username) }

        val token = URLEncoder.encode(rawToken, "utf-8")

        val tokenObject = jObject {
            "token" to token
        }.serialized()

        call.respondText(tokenObject, ContentType.Application.Json, HttpStatusCode.OK)
    }
}


suspend fun PipelineContext<Unit, ApplicationCall>.validateMail(mail: String) {
    if (!mail.matches(mailValidation.toRegex())) {
        badRequest("Invalid mail")
    }
}