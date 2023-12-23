package busTrackerApi.db

import busTrackerApi.config.*
import busTrackerApi.config.EnvVariables.alreadyLoadedDb
import busTrackerApi.config.EnvVariables.reloadDb
import busTrackerApi.db.models.Itinerary
import busTrackerApi.db.models.Shape
import busTrackerApi.db.models.StopOrder
import busTrackerApi.extensions.get
import busTrackerApi.extensions.mapAsync
import busTrackerApi.extensions.removeFirstLine
import busTrackerApi.extensions.toEnumeration
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.gildor.coroutines.okhttp.await
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


private const val sequenceChunkSize = 10_000

suspend fun loadDataIntoDb() = coroutineScope {
    if (!reloadDb || alreadyLoadedDb) return@coroutineScope

    val allStopsStream = getFileAsStreamFromGtfs("stops.txt")
    val allStopsTimesStream = getStopTimesFileAsStreamFromGtfs()
    val allShapesStream = getShapesFileAsStreamFromGtfs()
    val allItinerariesStream = getFileAsStreamFromGtfs("trips.txt")
    val allStopsInfoStream = getFileAsStreamFromInfo()

    awaitAll(
        async(Dispatchers.IO) {
            reader.openAsync(allStopsStream) {
                val stops = readAllWithHeaderAsSequence()
                    .filter { it["stop_id"]?.contains("par") == true }
                    .distinctBy { it["stop_id"] }
                    .chunked(sequenceChunkSize)
                stopsCollection.drop()
                stops.forEach {
                    val parsed = it.mapAsync(::parseStops).toList()
                    stopsCollection.insertMany(parsed)
                }
            }
        },
        async(Dispatchers.IO) {
            reader.openAsync(allItinerariesStream) {
                val itineraries = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                itinerariesCollection.drop()
                itineraries.forEach {
                    val parsed = it.mapAsync(::parseItinerary).toList()
                    itinerariesCollection.insertMany(parsed)
                }
            }
        },
        async(Dispatchers.IO) {
            reader.openAsync(allShapesStream) {
                val shapes = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                shapesCollection.drop()
                shapes.forEach {
                    val parsed = it.mapAsync(::parseShape).toList()
                    shapesCollection.insertMany(parsed)
                }
            }
        },
        async(Dispatchers.IO) {
            infoReader.openAsync(allStopsInfoStream) {
                val stops = readAllWithHeaderAsSequence().distinct().chunked(sequenceChunkSize)
                stopsInfoCollection.drop()
                stops.forEach {
                    val parsed = it.mapAsync(::parseStopsInfo).toList()
                    stopsInfoCollection.insertMany(parsed)
                }
            }
        },
        async(Dispatchers.IO) {
            reader.openAsync(allStopsTimesStream) {
                val stops = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                stopsOrder.drop()
                stops.forEach {
                    val parsed = it.mapAsync(::parseStopsOrder).toList()
                    stopsOrder.insertMany(parsed)
                }
            }
        }
    )
    shapesCollection.createIndex(Indexes.ascending(Shape::itineraryId.name))
    itinerariesCollection.createIndex(Indexes.ascending(Itinerary::tripId.name, Itinerary::itineraryCode.name))
    stopsOrder.createIndex(Indexes.ascending(StopOrder::tripId.name))
    alreadyLoadedDb = true
}

suspend fun downloadToTempFile(url: String): File = httpClient.get(url).await().use { response ->
    val tempFile = createTempFile().toFile()
    tempFile.deleteOnExit()
    response.body?.byteStream()?.use { download ->
        tempFile.outputStream().use {
            download.copyTo(it)
        }
    }
    return@use tempFile
}

suspend fun getFileAsStreamFromGtfs(file: String) = SequenceInputStream(
    listOf(
        File("${EnvVariables.metroGtfs.value()}/$file").inputStream(),
        File("${EnvVariables.trainGtfs.value()}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.tranviaGtfs.value()}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.interurbanGtfs.value()}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.urbanGtfs.value()}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.emtGtfs.value()}/$file").removeFirstLine().inputStream()
    ).toEnumeration()
)

suspend fun getStopTimesFileAsStreamFromGtfs(file: String = "stop_times.txt") = SequenceInputStream(
    listOf(
        File("${EnvVariables.interurbanGtfs.value()}/$file").inputStream(),
        File("${EnvVariables.urbanGtfs.value()}/$file").removeFirstLine().inputStream(),
    ).toEnumeration()
)

suspend fun getShapesFileAsStreamFromGtfs(file: String = "shapes.txt") = SequenceInputStream(
    listOf(
        File("${EnvVariables.interurbanGtfs.value()}/$file").inputStream(),
        File("${EnvVariables.urbanGtfs.value()}/$file").removeFirstLine().inputStream(),
    ).toEnumeration()
)

suspend fun getFileAsStreamFromInfo() = SequenceInputStream(
    listOf(
        File(EnvVariables.metroInfo.value()).inputStream(),
        File(EnvVariables.trainInfo.value()).removeFirstLine().inputStream(),
        File(EnvVariables.tranviaInfo.value()).removeFirstLine().inputStream(),
    ).toEnumeration()
)
