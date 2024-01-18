package busTrackerApi.routing.stops.train

import arrow.core.continuations.either
import arrow.core.getOrElse
import busTrackerApi.db.getStopNameById
import busTrackerApi.extensions.bindJson
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Coordinates
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.trainRouted.trainCodMode
import simpleJson.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

suspend fun parseTrainToStopTimes(json: JsonNode, coordinates: Coordinates, stopName: String, stopCode: String) =
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

fun createTrainFailedTimes(name: String, coordinates: Coordinates, stopCode: String) = StopTimes(
    codMode = trainCodMode.toInt(),
    stopName = name,
    coordinates = coordinates,
    arrives = null,
    incidents = emptyList(),
    simpleStopCode = stopCode
)