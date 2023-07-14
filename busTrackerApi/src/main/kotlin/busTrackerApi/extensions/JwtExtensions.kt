package busTrackerApi.extensions

import arrow.core.Either
import busTrackerApi.exceptions.BusTrackerException.Unauthorized
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import java.util.*
import kotlin.time.Duration

fun JWTCreator.Builder.withExpiresIn(duration: Duration): JWTCreator.Builder =
    withExpiresAt(Date(System.currentTimeMillis() + duration.inWholeMilliseconds))

fun JWTVerifier.verifyAndWrap(rawToken: String) = Either.catch {
    verify(rawToken)
}.mapLeft { Unauthorized(it.message) }