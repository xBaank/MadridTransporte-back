package busTrackerApi.routing.users

import arrow.core.Either
import arrow.core.continuations.either
import busTrackerApi.config.*
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.exceptions.BusTrackerException.*
import busTrackerApi.extensions.*
import busTrackerApi.routing.Response
import busTrackerApi.routing.Response.*
import busTrackerApi.utils.Call
import busTrackerApi.utils.accessTokenObject
import com.auth0.jwt.JWTVerifier
import com.toxicbakery.bcrypt.Bcrypt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.simplejavamail.api.mailer.Mailer
import simpleJson.asString
import simpleJson.deserialized
import simpleJson.get

val userRepo by inject<CoroutineDatabase>()
val authSigner by inject<Signer>(AuthSignerQualifier)
val resetPasswordSigner by inject<Signer>(ResetPasswordSignerQualifier)
val registerSigner by inject<Signer>(RegisterSignerQualifier)
val verifier by inject<JWTVerifier>()
val mailer by inject<Mailer>()
//TODO maybe move redirect urls to the body?
suspend fun Call.register(): Either<BusTrackerException, Response> = either {
    val user = call.receiveText().deserialized().bindMap()
    val backUrl = call.request.queryParameters.getWrapped("backUrl").bind()
    val redirectUrl = call.request.queryParameters.getWrapped("redirectUrl").bind()
    val userTyped = createUser(user).bind()

    val userExists = userRepo.getCollection<User>().findOne(User::email eq userTyped.email) != null
    if (userExists) shift<Conflict>(Conflict("User already exists"))

    val inserted = userRepo.getCollection<User>().insertOne(userTyped)
    if (!inserted.wasAcknowledged()) throw InternalServerError("Failed to insert user")

    val rawToken = registerSigner { withClaim("email", userTyped.email) }
    val verifyUrl = "${backUrl.encodeURLParameter()}/v1/users/verify?token=${rawToken.encodeURLParameter()}&redirectUrl=${redirectUrl.encodeURLParameter()}"

    sendAccountVerification(userTyped, verifyUrl)

    ResponseRaw(HttpStatusCode.Created)
}

suspend fun Call.verify(): Either<BusTrackerException, Response> = either {
    val redirectUrl = call.request.queryParameters.getWrapped("redirectUrl").bind()
    val rawToken = call.request.queryParameters.getWrapped("token").bind()
    val token = verifier.verifyAndWrap(rawToken).bind()
    val scope = token.getClaim("scope").asString() ?: shift(BadRequest("Scope not found"))
    val email = token.getClaim("email").asString() ?: shift(BadRequest("Email not found"))

    if (scope != RegisterScope) shift<BusTrackerException>(BadRequest("Invalid scope"))

    val user = userRepo.getCollection<User>().findOne(User::email eq email) ?:
        shift(NotFound("User not found"))

    if (user.verified) shift<BusTrackerException>(BadRequest("User already verified"))

    userRepo.getCollection<User>().updateOne(user.copy(verified = true))

    ResponseRedirect(redirectUrl)
}

suspend fun Call.login(): Either<BusTrackerException, Response> = either {
    val user = call.receiveText().deserialized().bindMap()
    val email = user["email"].asString().bindMap()
    val password = user["password"].asString().bindMap()

    val userTyped: User = userRepo.getCollection<User>().findOne(User::email eq email) ?:
    shift(NotFound("User not found"))

    if (!userTyped.verified) shift<BusTrackerException>(BadRequest("User not verified"))
    if (!Bcrypt.verifyHash(password, userTyped.password)) shift<BusTrackerException>(Unauthorized("Invalid password"))

    val rawToken = authSigner { withClaim("email", userTyped.email) }
    val tokenObject = accessTokenObject(rawToken)

   ResponseJson(tokenObject, HttpStatusCode.OK)
}

suspend fun Call.sendResetPassword() = either {
    val user = call.receiveText().deserialized().bindMap()
    val email = user["email"].asString().bindMap()
    val redirectUrl = call.request.queryParameters.getWrapped("redirectUrl").bind()

    val userTyped: User = userRepo.getCollection<User>().findOne(User::email eq email) ?:
    shift(NotFound("User not found"))

    val rawToken = resetPasswordSigner { withClaim("email", userTyped.email) }
    val redirectUrlWithToken = "$redirectUrl?token=${rawToken.encodeURLParameter()}"

    sendResetPassword(userTyped, redirectUrlWithToken)

    ResponseRaw(HttpStatusCode.OK)
}

suspend fun Call.resetPassword() = either {
    val token = call.request.queryParameters.getWrapped("token").bind()
    val rawToken = verifier.verifyAndWrap(token).bind()
    val email: String = rawToken.getClaim("email").asString() ?: shift(BadRequest("Email not found"))

    val scope = rawToken.getClaim("scope").asString() ?: shift(BadRequest("Scope not found"))
    if (scope != ResetPasswordScope) shift<BusTrackerException>(BadRequest("Invalid scope"))

    val newPass = call.receiveText().deserialized()
        .get("password").asString().bindMap()
        .validatePassword().bind()

    val userTyped: User = userRepo.getCollection<User>().findOne(User::email eq email) ?:
    shift(NotFound("User not found"))

    val user = userTyped.copy(password = Bcrypt.hashAsString(newPass, saltRounds))
    userRepo.getCollection<User>().updateOne(user)

    ResponseRaw(HttpStatusCode.OK)
}

