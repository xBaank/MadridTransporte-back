package busTrackerApi.plugins

import busTrackerApi.getenvOrThrow
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt("user") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(getenvOrThrow("JWT_SECRET")))
                    .withAudience(getenvOrThrow("JWT_AUDIENCE"))
                    .withIssuer(getenvOrThrow("JWT_ISSUER"))
                    .build()
            )
        }
    }
}