package api.routing.lines.train

import api.routing.lines.linesConfigF
import common.utils.trainCodMode
import io.ktor.server.routing.*

fun Route.trainLinesRouting() = route("/train") {
    linesConfigF(trainCodMode)
}
