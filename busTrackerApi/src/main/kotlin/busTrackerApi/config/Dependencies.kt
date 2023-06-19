package busTrackerApi.config

import busTrackerApi.getenvOrThrow
import busTrackerApi.withExpiresIn
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.mailer.MailerBuilder
import kotlin.time.Duration.Companion.minutes

private typealias SignerBuilder = (f: JWTCreator.Builder.() -> Unit) -> JWTCreator.Builder
typealias Signer = (f: JWTCreator.Builder.() -> Unit) -> String

const val saltRounds = 10


val dbModule = module {
    val dotenv = dotenv {
        ignoreIfMissing = true
        systemProperties = true
    }

    val client = KMongo
        .createClient(getenvOrThrow("MONGO_CONNECTION_STRING"))
        .coroutine
        .getDatabase(getenvOrThrow("MONGO_DATABASE_NAME"))

    val mailer = MailerBuilder
        .withSMTPServer(
            /* host = */ getenvOrThrow("STMP_HOST"),
            /* port = */ getenvOrThrow("STMP_PORT").toInt(),
            /* username = */ getenvOrThrow("STMP_USERNAME"),
            /* password = */ getenvOrThrow("STMP_PASSWORD")
        )
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .buildMailer()

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(getenvOrThrow("JWT_SECRET")))
        .withAudience(getenvOrThrow("JWT_AUDIENCE"))
        .withIssuer(getenvOrThrow("JWT_ISSUER"))
        .build()

    val signer: SignerBuilder = {
        JWT.create()
            .withAudience(getenvOrThrow("JWT_AUDIENCE"))
            .withIssuer(getenvOrThrow("JWT_ISSUER"))
            .apply(it)
    }

    val authSigner: Signer = {
        signer {
            withClaim("scope", AuthScope)
            withExpiresIn(30.minutes)
            apply(it)
        }.sign(Algorithm.HMAC256(getenvOrThrow("JWT_SECRET")))
    }

    val registerSigner: Signer = {
        signer {
            withClaim("scope", RegisterScope)
            apply(it)
        }.sign(Algorithm.HMAC256(getenvOrThrow("JWT_SECRET")))
    }

    val resetPasswordSigner: Signer = {
        signer {
            withClaim("scope", ResetPasswordScope)
            withExpiresIn(10.minutes)
            apply(it)
        }.sign(Algorithm.HMAC256(getenvOrThrow("JWT_SECRET")))
    }

    single { client }
    single { dotenv }
    single { mailer }
    single { jwtVerifier }
    single(ResetPasswordSignerQualifier) { resetPasswordSigner }
    single(AuthSignerQualifier) { authSigner }
    single(RegisterSignerQualifier) { registerSigner }
}

fun Application.configureDependencies() {
    koin { modules(dbModule) }
}