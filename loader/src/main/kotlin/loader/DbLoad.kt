package loader

import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.RenameCollectionOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import common.DB
import common.DB.db
import common.extensions.get
import common.extensions.mapAsync
import common.extensions.toEnumeration
import common.models.*
import common.queries.*
import common.utils.metroCodMode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import ru.gildor.coroutines.okhttp.await
import java.io.File
import java.io.SequenceInputStream
import java.util.UUID.randomUUID
import kotlin.io.path.createTempFile
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private val logger = LoggerFactory.getLogger("DbLoad")

val httpClient = OkHttpClient.Builder()
    .callTimeout(20.seconds.toJavaDuration())
    .connectTimeout(20.seconds.toJavaDuration())
    .readTimeout(20.seconds.toJavaDuration())
    .build()

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

suspend fun loadDataIntoDb(): Unit = coroutineScope {

    val allStopsStream = getFileAsStreamFromGtfs("stops.txt")
    val allRoutesStream = getFileAsStreamFromGtfs("routes.txt")
    val allStopsTimesStream = getStopTimesFileAsStreamFromGtfs()
    val allShapesStream = getShapesFileAsStreamFromGtfs()
    val allItinerariesStream = getFileAsStreamFromGtfs("trips.txt")
    val allStopsInfoStream = getFileAsStreamFromInfo()
    val allCalendars = getFileAsStreamFromGtfs("calendar.txt")

    val stopsCollectionNew: MongoCollection<Stop> by lazy { db.getCollection(randomUUID().toString()) }
    val stopsInfoCollectionNew: MongoCollection<StopInfo> by lazy { db.getCollection(randomUUID().toString()) }
    val itinerariesCollectionNew: MongoCollection<Itinerary> by lazy { db.getCollection(randomUUID().toString()) }
    val shapesCollectionNew: MongoCollection<Shape> by lazy { db.getCollection(randomUUID().toString()) }
    val stopsOrderCollectionNew: MongoCollection<StopOrder> by lazy { db.getCollection(randomUUID().toString()) }
    val calendarsCollectionNew: MongoCollection<Calendar> by lazy { db.getCollection(randomUUID().toString()) }
    val routesCollectionNew: MongoCollection<Route> by lazy { db.getCollection(randomUUID().toString()) }

    awaitAll(
        async {
            logger.info("Loading stops")
            reader.openAsync(allStopsStream) {
                val stops = readAllWithHeaderAsSequence()
                    .filter { it["stop_id"]?.contains("par") == true }
                    .distinctBy { it["stop_id"] }
                    .map(::parseStop)
                    .distinctBy { //This a hack to remove duplicates, since the same stop on metro can be repeated with different names
                        Pair(
                            if (it.codMode.toString() == metroCodMode) 1
                            else randomUUID().toString(),
                            it.stopName
                        )
                    }

                stopsCollectionNew.drop()

                stops.chunked(sequenceChunkSize).forEach {
                    stopsCollectionNew.insertMany(it)
                }
            }
            logger.info("Loaded stops")
        },
        async {
            logger.info("Loading routes")
            reader.openAsync(allRoutesStream) {
                val routes = readAllWithHeaderAsSequence()
                    .distinctBy { it["route_id"] }
                    .chunked(sequenceChunkSize)
                routesCollectionNew.drop()
                routes.forEach {
                    val parsed = it.mapAsync(::parseRoute).toList()
                    routesCollectionNew.insertMany(parsed)
                }
            }
            logger.info("Loaded routes")
        },
        async {
            logger.info("Loading itineraries")
            reader.openAsync(allItinerariesStream) {
                val itineraries = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                itinerariesCollectionNew.drop()
                itineraries.forEach {
                    val parsed = it.mapAsync(::parseItinerary).toList()
                    itinerariesCollectionNew.insertMany(parsed)
                }
            }
            logger.info("Loaded itineraries")
        },
        async {
            logger.info("Loading shapes")
            reader.openAsync(allShapesStream) {
                val shapes = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                shapesCollectionNew.drop()
                shapes.forEach {
                    val parsed = it.mapAsync(::parseShape).toList()
                    shapesCollectionNew.insertMany(parsed)
                }
            }
            logger.info("Loaded shapes")
        },
        async {
            logger.info("Loading stops info")
            infoReader.openAsync(allStopsInfoStream) {
                val stops = readAllWithHeaderAsSequence().distinct().chunked(sequenceChunkSize)
                stopsInfoCollectionNew.drop()
                stops.forEach {
                    val parsed = it.mapAsync(::parseStopInfo).toList()
                    stopsInfoCollectionNew.insertMany(parsed)
                }
            }
            logger.info("Loaded stops info")
        },
        async {
            logger.info("Loading stops order")
            reader.openAsync(allStopsTimesStream) {
                val stops = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                stopsOrderCollectionNew.drop()
                stops.forEach {
                    val parsed = it.mapAsync(::parseStopsOrder).toList()
                    stopsOrderCollectionNew.insertMany(parsed)
                }
            }
            logger.info("Loaded stops order")
        },
        async {
            logger.info("Loading calendars")
            reader.openAsync(allCalendars) {
                val stops = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                calendarsCollectionNew.drop()
                stops.forEach {
                    val parsed = it.mapAsync(::parseCalendar).toList()
                    calendarsCollectionNew.insertMany(parsed)
                }
            }
            logger.info("Loaded calendars")
        }
    )

    routesCollectionNew.createIndex(Indexes.ascending(Route::fullLineCode.name))
    routesCollectionNew.createIndex(Indexes.ascending(Route::codMode.name))
    shapesCollectionNew.createIndex(Indexes.ascending(Shape::itineraryId.name))
    itinerariesCollectionNew.createIndex(Indexes.ascending(Itinerary::tripId.name))
    itinerariesCollectionNew.createIndex(Indexes.ascending(Itinerary::fullLineCode.name))
    itinerariesCollectionNew.createIndex(Indexes.ascending(Itinerary::itineraryCode.name))
    stopsOrderCollectionNew.createIndex(Indexes.ascending(StopOrder::tripId.name))
    stopsOrderCollectionNew.createIndex(Indexes.ascending(StopOrder::fullStopCode.name))

    stopsCollectionNew.renameCollection(
        DB.stopsCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
    stopsInfoCollectionNew.renameCollection(
        DB.stopsInfoCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
    itinerariesCollectionNew.renameCollection(
        DB.itinerariesCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
    shapesCollectionNew.renameCollection(
        DB.shapesCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
    stopsOrderCollectionNew.renameCollection(
        DB.stopsOrderCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
    calendarsCollectionNew.renameCollection(
        DB.calendarsCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
    routesCollectionNew.renameCollection(
        DB.routesCollection.namespace,
        RenameCollectionOptions().dropTarget(true)
    )
}

suspend fun downloadToTempFile(url: String): File = httpClient.get(url).await().use { response ->
    logger.info("Downloading $url")
    val tempFile = createTempFile().toFile()
    tempFile.deleteOnExit()
    response.body?.byteStream()?.use { download ->
        tempFile.outputStream().use {
            download.copyTo(it)
        }
    }
    logger.info("Downloaded $url")
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
        File("${EnvVariables.emtGtfs.value()}/$file").removeFirstLine().inputStream(),
    ).toEnumeration()
)

suspend fun getShapesFileAsStreamFromGtfs(file: String = "shapes.txt") = SequenceInputStream(
    listOf(
        File("${EnvVariables.interurbanGtfs.value()}/$file").inputStream(),
        File("${EnvVariables.urbanGtfs.value()}/$file").removeFirstLine().inputStream(),
        File("${EnvVariables.emtGtfs.value()}/$file").removeFirstLine().inputStream(),
    ).toEnumeration()
)

suspend fun getFileAsStreamFromInfo() = SequenceInputStream(
    listOf(
        File(EnvVariables.metroInfo.value()).inputStream(),
        File(EnvVariables.trainInfo.value()).removeFirstLine().inputStream(),
        File(EnvVariables.tranviaInfo.value()).removeFirstLine().inputStream(),
    ).toEnumeration()
)
