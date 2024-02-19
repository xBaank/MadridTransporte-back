package api.routing.stops.train

import api.db.getStopNameById
import api.extensions.bindJson
import api.routing.stops.Arrive
import api.routing.stops.Coordinates
import api.routing.stops.StopTimes
import api.routing.stops.trainRouted.trainCodMode
import arrow.core.getOrElse
import arrow.core.raise.either
import simpleJson.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

suspend fun extractTrainStopTimes(json: JsonNode, coordinates: Coordinates, stopName: String, stopCode: String) =
    either {
        val commercialPaths = json["commercialPaths"].asArray().bindJson()

        val arrives = commercialPaths.map {
            val departureSteps = it["passthroughStep"]["departurePassthroughStepSides"]
            val destinationCode = it["commercialPathInfo"]["commercialDestinationStationCode"].asString().bindJson()
            val destinationName = getStopNameById(destinationCode).bind()
            val line = it["commercialPathInfo"]["line"].asString().getOrElse { "" }
            val delay = departureSteps["forecastedOrAuditedDelay"].asInt().bindJson().seconds
            val plannedTime = departureSteps["plannedTime"].asLong().bindJson().milliseconds
            Arrive(
                line = line,
                codMode = trainCodMode.toInt(),
                anden = departureSteps["plannedPlatform"].asString().bindJson().toInt(),
                destination = destinationName,
                estimatedArrive = (plannedTime + delay).inWholeMilliseconds
            )
        }

        StopTimes(
            trainCodMode.toInt(),
            stopName,
            coordinates,
            arrives,
            listOf(),
            stopCode
        )
    }