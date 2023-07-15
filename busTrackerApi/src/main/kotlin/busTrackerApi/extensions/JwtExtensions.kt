package busTrackerApi.extensions

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException.Unauthorized
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import io.ktor.server.auth.jwt.*
import java.util.*
import kotlin.time.Duration

fun JWTCreator.Builder.withExpiresIn(duration: Duration): JWTCreator.Builder =
    withExpiresAt(Date(System.currentTimeMillis() + duration.inWholeMilliseconds))

fun JWTVerifier.verifyAndWrap(rawToken: String) = Either.catch {
    verify(rawToken)
}.mapLeft { Unauthorized(it.message) }

fun JWTPayloadHolder?.getWrapped(key: String) = this?.get(key)?.right() ?: Unauthorized("Missing $key").left()