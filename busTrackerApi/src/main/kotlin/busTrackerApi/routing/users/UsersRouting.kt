package busTrackerApi.routing.users

import busTrackerApi.extensions.handleError
import busTrackerApi.extensions.handleResponse
import io.ktor.server.routing.*


fun Route.authRouting() {
    post("/register") {
        register().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    get("/verify") {
        verify().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    post("/login") {
          login().fold(
                { handleError(it) },
                { handleResponse(it) }
          )
    }

    post("/send-reset-password") {
        sendResetPassword().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }

    put("/reset-password") {
        resetPassword().fold(
            { handleError(it) },
            { handleResponse(it) }
        )
    }
}


