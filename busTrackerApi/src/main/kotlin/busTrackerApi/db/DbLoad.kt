package busTrackerApi.db

import busTrackerApi.config.*
import busTrackerApi.config.EnvVariables.reloadDb
import busTrackerApi.extensions.get
import busTrackerApi.extensions.removeFirstLine
import busTrackerApi.extensions.toEnumeration
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.mongodb.client.model.Filters
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

private val infoReader = csvReader {}


private const val sequenceChunkSize = 1000

suspend fun loadDataIntoDb() = coroutineScope {
    if (!reloadDb) return@coroutineScope

    val allStopsStream = getFileAsStreamFromGtfs("stops.txt")
    val allStopsTimesStream = getFileAsStreamFromGtfs("stop_times.txt")
    val allItinerariesStream = getFileAsStreamFromGtfs("trips.txt")
    val allStopsInfoStream = getFileAsStreamFromInfo()

    awaitAll(
        async {
            reader.openAsync(allStopsStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStops).chunked(sequenceChunkSize)
                stopsCollection.deleteMany(filter = Filters.empty())
                stops.forEach { stopsCollection.insertMany(it) }
            }
        },
        async {
            reader.openAsync(allItinerariesStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseItineraries).chunked(sequenceChunkSize)
                itinerariesCollection.deleteMany(filter = Filters.empty())
                stops.forEach { itinerariesCollection.insertMany(it) }
            }
        },
        async {
            infoReader.openAsync(allStopsInfoStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStopsInfo).distinct().chunked(sequenceChunkSize)
                stopsInfoCollection.deleteMany(filter = Filters.empty())
                stops.forEach { stopsInfoCollection.insertMany(it) }
            }
        },
        async {
            reader.openAsync(allStopsTimesStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStopsOrder).chunked(sequenceChunkSize)
                stopsOrder.deleteMany(filter = Filters.empty())
                stops.forEach { stopsOrder.insertMany(it) }
            }
        }
    )
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

fun getFileAsStreamFromInfo() = SequenceInputStream(
    listOf(
        File(EnvVariables.metroInfo).inputStream(),
        File(EnvVariables.trainInfo).removeFirstLine().inputStream(),
        File(EnvVariables.tranviaInfo).removeFirstLine().inputStream(),
    ).toEnumeration()
)
