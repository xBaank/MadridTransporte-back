package busTrackerApi.db

import busTrackerApi.config.*
import busTrackerApi.config.EnvVariables.reloadDb
import busTrackerApi.extensions.get
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
}

private const val sequenceChunkSize = 1000

suspend fun loadDataIntoDb() = coroutineScope {
    if (!reloadDb) return@coroutineScope

    val allStopsStream = getFileAsStreamFromGtfs("stops.txt")
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
            reader.openAsync(allStopsInfoStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStopsInfo).chunked(sequenceChunkSize)
                stopsInfoCollection.deleteMany(filter = Filters.empty())
                stops.forEach { stopsInfoCollection.insertMany(it) }
            }
        },
        async {
            reader.openAsync(allStopsInfoStream) {
                val stops = readAllWithHeaderAsSequence().map(::parseStopsInfo).chunked(sequenceChunkSize)
                stopsInfoCollection.deleteMany(filter = Filters.empty())
                stops.forEach { stopsInfoCollection.insertMany(it) }
            }
        }
    )
}

fun downloadToTempFile(url: String): File = httpClient.get(url).execute().use { response ->
    val tempFile = createTempFile().toFile()
    response.body?.byteStream()?.copyTo(tempFile.outputStream())
    return@use tempFile
}

fun getFileAsStreamFromGtfs(file: String) = SequenceInputStream(
    listOf(
        File("${EnvVariables.metroGtfs}/$file").inputStream(),
        File("${EnvVariables.trainGtfs}/$file").inputStream(),
        File("${EnvVariables.tranviaGtfs}/$file").inputStream(),
        File("${EnvVariables.interurbanGtfs}/$file").inputStream(),
        File("${EnvVariables.urbanGtfs}/$file").inputStream(),
        File("${EnvVariables.emtGtfs}/$file").inputStream()
    ).toEnumeration()
)

fun getFileAsStreamFromInfo() = SequenceInputStream(
    listOf(
        File(EnvVariables.metroInfo).inputStream(),
        File(EnvVariables.trainInfo).inputStream(),
    ).toEnumeration()
)
