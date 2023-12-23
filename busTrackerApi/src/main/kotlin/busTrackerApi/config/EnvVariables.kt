package busTrackerApi.config

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import busTrackerApi.db.downloadToTempFile
import busTrackerApi.exceptions.BusTrackerException
import busTrackerApi.extensions.unzip
import busTrackerApi.utils.SuspendingLazy
import java.io.File

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
    "https://opendata.arcgis.com/api/v3/datasets/f3859438e5504a6b9ca745880f72ef1b_0/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"
private const val defaultTrainInfo =
    "https://opendata.arcgis.com/api/v3/datasets/9e353bbf4c5d4bea87f01d6d579d06ab_0/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"
private const val defaultTranviaInfo =
    "https://opendata.arcgis.com/api/v3/datasets/624dfeafb4d64580aa2ac5f24d8e8614_0/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"

object EnvVariables {
    val notificationDelayTimeSeconds by lazy {
        getenvWrapped("NOTIFICATION_DELAY_TIME_SECONDS").map(String::toLong).getOrElse { 60 }
    }
    val port by lazy { getenvWrapped("PORT").map(String::toInt).getOrElse { 8080 } }
    val mongoConnectionString by lazy { getenvWrapped("MONGO_CONNECTION_STRING") }
    val serviceJson by lazy { getenvWrapped("SERVICE_JSON") }
    val reloadDb by lazy { getenvWrapped("RELOAD_DB").map(String::toBoolean).getOrElse { true } }
    var alreadyLoadedDb: Boolean = false
    val metroGtfs =
        SuspendingLazy { getenvWrapped("METRO_GTFS").map(::File).getOrElse { downloadToTempFile(defaultMetroGtfs) }.unzip() }
    val tranviaGtfs =
        SuspendingLazy { getenvWrapped("TRANVIA_GTFS").map(::File).getOrElse { downloadToTempFile(defaultTranviaGtfs) }.unzip() }
    val emtGtfs =
        SuspendingLazy { getenvWrapped("EMT_GTFS").map(::File).getOrElse { downloadToTempFile(defaultEmtGtfs) }.unzip() }
    val trainGtfs =
        SuspendingLazy { getenvWrapped("TRAIN_GTFS").map(::File).getOrElse { downloadToTempFile(defaultTrainGtfs) }.unzip() }
    val interurbanGtfs =
        SuspendingLazy { getenvWrapped("INTERURBAN_GTFS").map(::File).getOrElse { downloadToTempFile(defaultInterurbanGtfs) }.unzip() }
    val urbanGtfs =
        SuspendingLazy { getenvWrapped("URBAN_GTFS").map(::File).getOrElse { downloadToTempFile(defaultUrbanGtfs) }.unzip() }
    val metroInfo =
        SuspendingLazy { getenvWrapped("METRO_INFO").getOrElse { downloadToTempFile(defaultMetroInfo).path } }
    val trainInfo =
        SuspendingLazy { getenvWrapped("TRAIN_INFO").getOrElse { downloadToTempFile(defaultTrainInfo).path } }
    val tranviaInfo =
        SuspendingLazy { getenvWrapped("TRANVIA_INFO").getOrElse { downloadToTempFile(defaultTranviaInfo).path } }


    private fun getenvOrNull(key: String): String? =
        System.getenv(key) ?: System.getProperty(key) ?: null

    private fun getenvWrapped(key: String): Either<BusTrackerException.InternalServerError, String> =
        getenvOrNull(key)?.right() ?: BusTrackerException.InternalServerError("Environment variable $key not found")
            .left()
}