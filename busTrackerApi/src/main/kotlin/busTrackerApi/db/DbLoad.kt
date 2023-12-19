package busTrackerApi.db

import busTrackerApi.config.*
import busTrackerApi.config.EnvVariables.alreadyLoadedDb
import busTrackerApi.config.EnvVariables.reloadDb
import busTrackerApi.extensions.get
import busTrackerApi.extensions.removeFirstLine
import busTrackerApi.extensions.toEnumeration
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.SequenceInputStream
import kotlin.io.path.createTempFile

private val reader = csvReader {
    escapeChar = '\''
    skipEmptyLine = true
    autoRenameDuplicateHeaders = true
}

private val infoReader = csvReader {
    skipEmptyLine = true
    insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.EMPTY_STRING
    excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.TRIM
}


private const val sequenceChunkSize = 100000

suspend fun loadDataIntoDb() = coroutineScope {
    if (!reloadDb || alreadyLoadedDb) return@coroutineScope

    val allStopsStream = getFileAsStreamFromGtfs("stops.txt")
    val allStopsTimesStream = getStopTimesFileAsStreamFromGtfs()
    val allItinerariesStream = getFileAsStreamFromGtfs("trips.txt")
    val allStopsInfoStream = getFileAsStreamFromInfo()

    awaitAll(
        async {
            reader.openAsync(allStopsStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStops).chunked(sequenceChunkSize)
                stopsCollection.drop()
                stops.forEach { stopsCollection.insertMany(it) }
            }
        },
        async {
            reader.openAsync(allItinerariesStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseItineraries).chunked(sequenceChunkSize)
                itinerariesCollection.drop()
                stops.forEach { itinerariesCollection.insertMany(it) }
            }
        },
        async {
            infoReader.openAsync(allStopsInfoStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStopsInfo).distinct().chunked(sequenceChunkSize)
                stopsInfoCollection.drop()
                stops.forEach { stopsInfoCollection.insertMany(it) }
            }
        },
        async {
            reader.openAsync(allStopsTimesStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStopsOrder).chunked(sequenceChunkSize)
                stopsOrder.drop()
                stops.forEach { stopsOrder.insertMany(it) }
            }
        }
    )

    alreadyLoadedDb = true
}

fun downloadToTempFile(url: String): File = httpClient.get(url).execute().use { response ->
    val tempFile = createTempFile().toFile()
    tempFile.deleteOnExit()
    response.body?.byteStream()?.use { download ->
        tempFile.outputStream().use {
            download.copyTo(it)
        }
    }
    return@use tempFile
}

fun getFileAsStreamFromGtfs(file: String) = SequenceInputStream(
    listOf(
        File("${EnvVariables.metroGtfs}/$file").inputStream(),
        File("${EnvVariables.trainGtfs}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.tranviaGtfs}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.interurbanGtfs}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.urbanGtfs}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.emtGtfs}/$file").removeFirstLine().inputStream()
    ).toEnumeration()
)

fun getStopTimesFileAsStreamFromGtfs(file: String = "stop_times.txt") = SequenceInputStream(
    listOf(
        File("${EnvVariables.interurbanGtfs}/$file").inputStream(),
        File("${EnvVariables.urbanGtfs}/$file").removeFirstLine().inputStream(),
    ).toEnumeration()
)

fun getFileAsStreamFromInfo() = SequenceInputStream(
    listOf(
        File(EnvVariables.metroInfo).inputStream(),
        File(EnvVariables.trainInfo).removeFirstLine().inputStream(),
        File(EnvVariables.tranviaInfo).removeFirstLine().inputStream(),
    ).toEnumeration()
)
