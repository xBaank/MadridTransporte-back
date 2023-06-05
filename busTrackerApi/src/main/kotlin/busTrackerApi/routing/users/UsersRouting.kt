package busTrackerApi.routing.users

import io.ktor.server.routing.*

fun Route.usersRouting() = route("/users") {
    post("/register") {
        TODO()
    }
    post("/login") {
        TODO()
    }
}