package busTrackerApi.routing.metro

import busTrackerApi.extensions.handle
import io.ktor.server.application.*
import io.ktor.server.routing.*

//TODO Merge this into the stops path, to do it we need to relation this codes with the ones crtm provides
fun Route.timesRouting() {
    get("/times") {
        handle { getTimes() }
    }
    get("/times/{id}") {
        handle { getTimes(call.parameters["id"]) }
    }
}