package busTrackerApi.routing.abono

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.routing.*

fun Route.abonoRouting() {
    get("{id}") {
        getAbono().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}