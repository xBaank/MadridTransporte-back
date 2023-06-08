package busTrackerApi.plugins

import busTrackerApi.getenvOrThrow
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

typealias Signer = (f: JWTCreator.Builder.() -> Unit) -> String

const val saltRounds = 10

private val dbModule = module {
    val dotenv = dotenv()

    val client = KMongo
        .createClient(dotenv.getenvOrThrow("MONGO_CONNECTION_STRING"))
        .coroutine
        .getDatabase(dotenv.getenvOrThrow("MONGO_DATABASE_NAME"))

    val mailer = MailerBuilder
        .withSMTPServer(
            /* host = */ dotenv.getenvOrThrow("STMP_HOST"),
            /* port = */ dotenv.getenvOrThrow("STMP_PORT").toInt(),
            /* username = */ dotenv.getenvOrThrow("STMP_USERNAME"),
            /* password = */ dotenv.getenvOrThrow("STMP_PASSWORD")
        )
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .buildMailer()

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(dotenv.getenvOrThrow("JWT_SECRET")))
        .withAudience(dotenv.getenvOrThrow("JWT_AUDIENCE"))
        .withIssuer(dotenv.getenvOrThrow("JWT_ISSUER"))
        .build()

    val signer: Signer = {
        JWT.create()
            .withAudience(dotenv.getenvOrThrow("JWT_AUDIENCE"))
            .withIssuer(dotenv.getenvOrThrow("JWT_ISSUER"))
            .apply(it)
            .sign(Algorithm.HMAC256(dotenv.getenvOrThrow("JWT_SECRET")))
    }

    single { client }
    single { dotenv }
    single { mailer }
    single { jwtVerifier }
    single { signer }
}

fun Application.configureDependencies() {
    koin { modules(dbModule) }

}