package busTrackerApi.config

import com.auth0.jwt.JWTVerifier
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject


fun Application.configureAuth() {
    val verifier by inject<JWTVerifier>()
    install(Authentication) {
        jwt("user") {
            verifier(verifier)
            validate {
                if (it.payload.getClaim("scope").asString() != AuthScope) null
                else JWTPrincipal(it.payload)
            }
        }
    }
}