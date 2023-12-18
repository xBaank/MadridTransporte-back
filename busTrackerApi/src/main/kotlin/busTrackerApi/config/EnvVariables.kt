package busTrackerApi.config

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.db.downloadToTempFile
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.unzip

private const val defaultMetroGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/5c7f2951962540d69ffe8f640d94c246/data"
private const val defaultTrainGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/1a25440bf66f499bae2657ec7fb40144/data"
private const val defaultTranviaGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/aaed26cc0ff64b0c947ac0bc3e033196/data"
private const val defaultEmtGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/868df0e58fca47e79b942902dffd7da0/data"
private const val defaultUrbanGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/357e63c2904f43aeb5d8a267a64346d8/data"
private const val defaultInterurbanGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/885399f83408473c8d815e40c5e702b7/data"
private const val defaultMetroInfo =
    "https://opendata.arcgis.com/api/v3/datasets/f3859438e5504a6b9ca745880f72ef1b_3/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"
private const val defaultTrainInfo =
    "https://opendata.arcgis.com/api/v3/datasets/9e353bbf4c5d4bea87f01d6d579d06ab_5/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"

object EnvVariables {
    val notificationDelayTimeSeconds by lazy {
        getenvWrapped("NOTIFICATION_DELAY_TIME_SECONDS").map(String::toLong).getOrElse { 60 }
    }
    val port by lazy { getenvWrapped("PORT").map(String::toInt).getOrElse { 8080 } }
    val mongoConnectionString by lazy { getenvWrapped("MONGO_CONNECTION_STRING") }
    val serviceJson by lazy { getenvWrapped("SERVICE_JSON") }
    val metroGtfs by lazy { getenvWrapped("METRO_GTFS").getOrElse { downloadToTempFile(defaultMetroGtfs).unzip() } }
    val tranviaGtfs by lazy { getenvWrapped("TRANVIA_GTFS").getOrElse { downloadToTempFile(defaultTranviaGtfs).unzip() } }
    val emtGtfs by lazy { getenvWrapped("EMT_GTFS").getOrElse { downloadToTempFile(defaultEmtGtfs).unzip() } }
    val trainGtfs by lazy { getenvWrapped("TRAIN_GTFS").getOrElse { downloadToTempFile(defaultTrainGtfs).unzip() } }
    val interurbanGtfs by lazy { getenvWrapped("INTERURBAN_GTFS").getOrElse { downloadToTempFile(defaultInterurbanGtfs).unzip() } }
    val urbanGtfs by lazy { getenvWrapped("URBAN_GTFS").getOrElse { downloadToTempFile(defaultUrbanGtfs).unzip() } }
    val metroInfo by lazy { getenvWrapped("METRO_INFO").getOrElse { defaultMetroInfo } }
    val trainInfo by lazy { getenvWrapped("TRAIN_INFO").getOrElse { defaultTrainInfo } }
    val reloadDb by lazy { getenvWrapped("RELOAD_DB").map(String::toBoolean).getOrElse { true } }

    private fun getenvOrNull(key: String): String? =
        System.getenv(key) ?: System.getProperty(key) ?: null

    private fun getenvWrapped(key: String): Either<BusTrackerException.InternalServerError, String> =
        getenvOrNull(key)?.right() ?: BusTrackerException.InternalServerError("Environment variable $key not found")
            .left()
}