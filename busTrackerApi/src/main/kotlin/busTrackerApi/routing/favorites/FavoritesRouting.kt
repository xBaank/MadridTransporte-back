package busTrackerApi.routing.favorites

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.favoritesRouting() = authenticate("user") {
    post {
        createFavorite().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get {
        getFavorites().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{id}") {
        getFavorite().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    delete("/{id}") {
        deleteFavorite().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}

