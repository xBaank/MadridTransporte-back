package busTrackerApi.routing.favourites

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.favoritesRouting() = authenticate("user") {
    post {
        createFavourite().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get {
        getFavourites().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/{id}") {
        getFavourite().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    delete("/{id}") {
        deleteFavourite().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}

