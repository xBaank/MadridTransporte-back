package loader

import arrow.core.getOrElse
import common.EnvUtils.getenvWrapped
import common.utils.SuspendingLazy
import java.io.File

/*
I don't think we should depend on CRTM open data because they had removed data from before, each csv could have its
own separator, data is repeated in many cases... and the funniest part is that this was done with a budget of 344.677 €
Here is the deleted article https://shorturl.at/XJNOv (btw why was it deleted??)

4/01/2025
It happened again, modified/removed data and with incorrect values.
* */
private const val ref = "master"
private const val defaultMetroGtfs =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/google_transit_M4.zip"
private const val defaultTrainGtfs =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/google_transit_M5.zip"
private const val defaultTranviaGtfs =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/google_transit_M10.zip"
private const val defaultEmtGtfs =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/google_transit_M6.zip"
private const val defaultUrbanGtfs =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/google_transit_M9.zip"
private const val defaultInterurbanGtfs =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/google_transit_M89.zip"
private const val defaultMetroInfo =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/Metro_stations.csv"
private const val defaultTrainInfo =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/Train_stations.csv"
private const val defaultTranviaInfo =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/Tram_stations.csv"
private const val defaultTrainItineraries =
    "https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/$ref/Train_itineraries.csv"

object EnvVariables {
    val metroGtfs =
        SuspendingLazy {
            getenvWrapped("METRO_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultMetroGtfs) }.unzip()
        }
    val tranviaGtfs: SuspendingLazy<String> = SuspendingLazy {
        getenvWrapped("TRANVIA_GTFS").map(::File)
            .getOrElse { downloadToTempFile(defaultTranviaGtfs) }.unzip()
    }
    val emtGtfs =
        SuspendingLazy {
            getenvWrapped("EMT_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultEmtGtfs) }.unzip()
        }
    val trainGtfs =
        SuspendingLazy {
            getenvWrapped("TRAIN_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultTrainGtfs) }.unzip()
        }
    val interurbanGtfs =
        SuspendingLazy {
            getenvWrapped("INTERURBAN_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultInterurbanGtfs) }.unzip()
        }
    val urbanGtfs =
        SuspendingLazy {
            getenvWrapped("URBAN_GTFS").map(::File)
                .getOrElse { downloadToTempFile(defaultUrbanGtfs) }.unzip()
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
