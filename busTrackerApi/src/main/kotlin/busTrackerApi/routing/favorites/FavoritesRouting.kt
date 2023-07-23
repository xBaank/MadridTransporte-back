package busTrackerApi.routing.favorites

import busTrackerApi.extensions.handle
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.favoritesRouting() = authenticate("user") {
    post {
        createFavorite().handle()
    }

    get {
        getFavorites().handle()
    }

    get("/{id}") {
        getFavorite().handle()
    }

    delete("/{id}") {
        deleteFavorite().handle()
    }
}

