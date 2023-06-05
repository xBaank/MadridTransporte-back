package busTrackerApi.plugins

import busTrackerApi.getenvOrThrow
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.koin
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val dbModule = module {
    single { KMongo.createClient(getenvOrThrow("MONGO_CONNECTION_STRING")).getDatabase("busTracker").coroutine }
}

fun Application.configureDependencies() {
    koin {
        modules(dbModule)
    }

}