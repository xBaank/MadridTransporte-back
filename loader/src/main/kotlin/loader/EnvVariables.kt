package loader

import arrow.core.getOrElse
import common.EnvUtils.getenvWrapped
import common.utils.SuspendingLazy
import java.io.File

/*
I don't think we should depend on CRTM open data because they had removed data from before, each csv could have its
own separator, data is repeated in many cases... and the funniest part is that this was done with a budget of 344.677 €
Here is the deleted article https://shorturl.at/XJNOv (btw why was it deleted??)
*/
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

//Nota aclarativa del crtm: Los estandares son para debiles 🔥🔥
private const val defaultInterurbanGtfs =
    "https://www.arcgis.com/sharing/rest/content/items/885399f83408473c8d815e40c5e702b7/data"
private const val defaultMetroInfo =
    "https://hub.arcgis.com/api/v3/datasets/0a6c45e7bdd94679b67a2ae662c8838b_0/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"
private const val defaultTrainInfo =
    "https://hub.arcgis.com/api/v3/datasets/9e353bbf4c5d4bea87f01d6d579d06ab_0/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"
private const val defaultTranviaInfo =
    "https://hub.arcgis.com/api/v3/datasets/53c45916691a4256bf0f6f69fb0e182c_0/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"
private const val defaultTrainItineraries =
    "https://hub.arcgis.com/api/v3/datasets/9e353bbf4c5d4bea87f01d6d579d06ab_5/downloads/data?format=csv&spatialRefId=25830&where=1%3D1"

object EnvVariables {
    val metroGtfs =
        SuspendingLazy {
            getenvWrapped("METRO_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultMetroGtfs) }.unzip().`fix CRTM 💩`().toString()
        }
    val tranviaGtfs: SuspendingLazy<String> =
        SuspendingLazy {
            getenvWrapped("TRANVIA_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultTranviaGtfs) }.unzip().`fix CRTM 💩`().toString()
        }
    val emtGtfs =
        SuspendingLazy {
            getenvWrapped("EMT_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultEmtGtfs) }.unzip().`fix CRTM 💩`().toString()
        }
    val trainGtfs =
        SuspendingLazy {
            getenvWrapped("TRAIN_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultTrainGtfs) }.unzip().`fix CRTM 💩`().toString()
        }
    val interurbanGtfs =
        SuspendingLazy {
            getenvWrapped("INTERURBAN_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultInterurbanGtfs) }.unzip().`fix CRTM 💩`().toString()
        }
    val urbanGtfs =
        SuspendingLazy {
            getenvWrapped("URBAN_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultUrbanGtfs) }.unzip().`fix CRTM 💩`().toString()
        }

    val metroInfo =
        SuspendingLazy { getenvWrapped("METRO_INFO").getOrElse { downloadToTempFile(defaultMetroInfo).path } }
    val trainInfo =
        SuspendingLazy { getenvWrapped("TRAIN_INFO").getOrElse { downloadToTempFile(defaultTrainInfo).path } }
    val tranviaInfo =
        SuspendingLazy { getenvWrapped("TRANVIA_INFO").getOrElse { downloadToTempFile(defaultTranviaInfo).path } }
    val trainItineraries =
        SuspendingLazy { getenvWrapped("TRAIN_ITINERARIES").getOrElse { downloadToTempFile(defaultTrainItineraries).path } }

    val mongoConnectionString by lazy { getenvWrapped("MONGO_CONNECTION_STRING") }
}
