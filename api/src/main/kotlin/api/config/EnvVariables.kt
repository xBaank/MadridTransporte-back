package api.config

import arrow.core.getOrElse
import common.Env.getenvWrapped
import kotlin.time.Duration.Companion.seconds

object EnvVariables {
    val notificationDelayTimeSeconds by lazy {
        getenvWrapped("NOTIFICATION_DELAY_TIME_SECONDS").map(String::toLong).getOrElse { 60 }.seconds
    }
    val timeoutSeconds by lazy {
        getenvWrapped("SOAP_TIMEOUT").map(String::toLong).getOrElse { 30 }.seconds
    }
    val port by lazy { getenvWrapped("PORT").map(String::toInt).getOrElse { 8080 } }
    val mongoConnectionString by lazy { getenvWrapped("MONGO_CONNECTION_STRING") }
    val serviceJson by lazy { getenvWrapped("SERVICE_JSON") }
}