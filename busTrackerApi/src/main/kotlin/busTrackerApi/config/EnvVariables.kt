package busTrackerApi.config

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.exceptions.BusTrackerException

private const val defaultAllStopsUrl =
    "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/stops.json"
private const val defaultAllStopsInfoUrl =
    "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/stops-info.json"
private const val defaultTtinerariesUrl =
    "https://raw.githubusercontent.com/xBaank/bus-tracker-static/main/itineraries.json"

object EnvVariables {
    val notificationDelayTimeSeconds by lazy {
        getenvWrapped("NOTIFICATION_DELAY_TIME_SECONDS").map(String::toLong).getOrElse { 60 }
    }
    val port by lazy { getenvWrapped("PORT").map(String::toInt).getOrElse { 8080 } }
    val mongoConnectionString by lazy { getenvWrapped("MONGO_CONNECTION_STRING") }
    val serviceJson by lazy { getenvWrapped("SERVICE_JSON") }
    val allStopsUrl by lazy { getenvWrapped("ALL_STOPS_URL").getOrElse { defaultAllStopsUrl } }
    val allStopsInfoUrl by lazy { getenvWrapped("ALL_STOPS_INFO_URL").getOrElse { defaultAllStopsInfoUrl } }
    val itinerariesUrl by lazy { getenvWrapped("BUS_ITINERARIES_URL").getOrElse { defaultTtinerariesUrl } }

    private fun getenvOrNull(key: String): String? =
        System.getenv(key) ?: System.getProperty(key) ?: null

    private fun getenvWrapped(key: String): Either<BusTrackerException.InternalServerError, String> =
        getenvOrNull(key)?.right() ?: BusTrackerException.InternalServerError("Environment variable $key not found")
            .left()
}