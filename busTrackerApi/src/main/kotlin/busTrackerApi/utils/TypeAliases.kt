package busTrackerApi.utils

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

typealias Call = PipelineContext<Unit, ApplicationCall>