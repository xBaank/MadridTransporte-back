package busTrackerApi.routing.users

import busTrackerApi.extensions.handle
import io.ktor.server.routing.*


fun Route.authRouting() {
    post("/register") {
        handle { register() }
    }

    get("/verify") {
        handle { verify() }
    }

    post("/login") {
        handle { login() }
    }

    post("/send-reset-password") {
        handle { sendResetPassword() }
    }

    put("/reset-password") {
        handle { resetPassword() }
    }
}


