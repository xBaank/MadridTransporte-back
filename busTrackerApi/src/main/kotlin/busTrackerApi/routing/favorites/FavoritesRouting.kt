package busTrackerApi.routing.favorites

import busTrackerApi.extensions.handle
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.favoritesRouting() = authenticate("user") {
    post {
        handle { createFavorite() }
    }

    get {
        handle { getFavorites() }
    }

    get("/{id}") {
        handle { getFavorite() }
    }

    delete("/{id}") {
        handle { deleteFavorite() }
    }
}

