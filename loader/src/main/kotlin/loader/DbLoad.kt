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
import common.utils.Loom
import common.utils.SuspendingLazy
import common.utils.metroCodMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
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

private val gtfsReader = csvReader {
    escapeChar = '\''
    skipEmptyLine = true
    autoRenameDuplicateHeaders = true
}

private val stopsInfoReader = csvReader {
    skipEmptyLine = true
    insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.EMPTY_STRING
    excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.TRIM
}

private val itinerariesReader = csvReader {
    skipEmptyLine = true
    insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.EMPTY_STRING
    excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.TRIM
}


private const val sequenceChunkSize = 10_000

suspend fun loadDataIntoDb(): Unit = withContext(Dispatchers.Loom) {
    val stopsCollectionNew: MongoCollection<Stop> by lazy { db.getCollection(randomUUID().toString()) }
    val stopsInfoCollectionNew: MongoCollection<StopInfo> by lazy { db.getCollection(randomUUID().toString()) }
    val itinerariesCollectionNew: MongoCollection<Itinerary> by lazy { db.getCollection(randomUUID().toString()) }
    val shapesCollectionNew: MongoCollection<Shape> by lazy { db.getCollection(randomUUID().toString()) }
    val stopsOrderCollectionNew: MongoCollection<StopOrder> by lazy { db.getCollection(randomUUID().toString()) }
    val calendarsCollectionNew: MongoCollection<Calendar> by lazy { db.getCollection(randomUUID().toString()) }
    val routesCollectionNew: MongoCollection<Route> by lazy { db.getCollection(randomUUID().toString()) }

    try {
        val allGtfs = listOf(
            EnvVariables.metroGtfs,
            EnvVariables.trainGtfs,
            EnvVariables.tranviaGtfs,
            EnvVariables.interurbanGtfs,
            EnvVariables.urbanGtfs,
            EnvVariables.emtGtfs
        )

        val stopsInfoFiles = listOf(EnvVariables.metroInfo, EnvVariables.trainInfo, EnvVariables.tranviaInfo)
        val trainFiles = listOf(EnvVariables.trainItineraries)

        val repeatedToOriginalStops = mutableMapOf<String, String>()
        val uniqueMetroStops = mutableMapOf<Pair<Int, String>, String>()

        awaitAll(
            async {
                logger.info("Loading stops")
                gtfsReader.openAsync(getFromGtfs("stops.txt", allGtfs)) {
                    val stops = readAllWithHeaderAsSequence()
                        .filter { it["stop_id"]?.contains("par") == true }
                        .distinctBy { it["stop_id"] }
                        .map(::parseStop)
                        .mapNotNull { it }
                        .onEach {
                            if (it.codMode.toString() != metroCodMode) return@onEach
                            val key = it.codMode to it.stopName
                            val uniqueMetroStop = uniqueMetroStops[key]
                            if (uniqueMetroStop != null) repeatedToOriginalStops[it.fullStopCode] = uniqueMetroStop
                            else if (it.codMode.toString() == metroCodMode) uniqueMetroStops[key] = it.fullStopCode
                        }
                        .distinctBy { //This a hack to remove duplicates, since the same stop on metro can be repeated with different names
                            Pair(
                                if (it.codMode.toString() == metroCodMode) 1
                                else randomUUID().toString(),
                                it.stopName
                            )
                        }
                    stops.chunked(sequenceChunkSize).forEach {
                        if (it.isNotEmpty()) stopsCollectionNew.insertMany(it)
                    }
                }
                logger.info("Loaded stops")
            },
            async {
                logger.info("Loading routes from gtfs")
                gtfsReader.openAsync(getFromGtfs("routes.txt", allGtfs)) {
                    val routes = readAllWithHeaderAsSequence()
                        .distinctBy { it["route_id"] }
                        .chunked(sequenceChunkSize)
                    routes.forEach {
                        val parsed = it.mapAsync(::parseRoute).mapNotNull { it }
                        if (parsed.isNotEmpty()) routesCollectionNew.insertMany(parsed)
                    }
                }
                logger.info("Loaded routes from gtfs")
            },
            async {
                logger.info("Loading other routes")
                itinerariesReader.openAsync(getFromFile(trainFiles)) {
                    val routes = readAllWithHeaderAsSequence()
                        .distinctBy { it["IDFLINEA"] }
                        .chunked(sequenceChunkSize)
                    routes.forEach {
                        val parsed = it.mapAsync(::parseRoute).mapNotNull { it }
                        if (parsed.isNotEmpty()) routesCollectionNew.insertMany(parsed)
                    }
                }
                logger.info("Loaded other routes")
            },
            async {
                logger.info("Loading bus itineraries from gtfs")
                gtfsReader.openAsync(getFromGtfs("trips.txt", allGtfs)) {
                    val itineraries = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                    itineraries.forEach {
                        val parsed = it.mapAsync(::parseItinerary).mapNotNull { it }
                        if (parsed.isNotEmpty()) itinerariesCollectionNew.insertMany(parsed)
                    }
                }
                logger.info("Loaded bus itineraries gtfs")
            },
            async {
                logger.info("Loading itineraries from file")
                itinerariesReader.openAsync(getFromFile(trainFiles)) {
                    val itineraries = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                    itineraries.forEach {
                        val parsed = it.mapAsync(::parseItinerary).mapNotNull { it }
                        val parsedStops = it.mapAsync(::parseStopsOrder).mapNotNull { it }
                        if (parsed.isNotEmpty()) itinerariesCollectionNew.insertMany(parsed)
                        if (parsedStops.isNotEmpty()) stopsOrderCollectionNew.insertMany(parsedStops)
                    }
                }
                logger.info("Loaded itineraries file")
            },
            async {
                logger.info("Loading shapes")
                gtfsReader.openAsync(getFromGtfs("shapes.txt", allGtfs)) {
                    val shapes = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                    shapes.forEach {
                        val parsed = it.mapAsync(::parseShape).mapNotNull { it }
                        if (parsed.isNotEmpty()) shapesCollectionNew.insertMany(parsed)
                    }
                }
                logger.info("Loaded shapes")
            },
            async {
                logger.info("Loading stops info")
                stopsInfoReader.openAsync(getFromFile(stopsInfoFiles)) {
                    val stops = readAllWithHeaderAsSequence().distinct().chunked(sequenceChunkSize)
                    stops.forEach {
                        val parsed = it.mapAsync(::parseStopInfo).mapNotNull { it }
                        if (parsed.isNotEmpty()) stopsInfoCollectionNew.insertMany(parsed)
                    }
                }
                logger.info("Loaded stops info")
            },
            async {
                logger.info("Loading bus stops order from gtfs")
                gtfsReader.openAsync(getFromGtfs("stop_times.txt", allGtfs)) {
                    val stops = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                    stops.forEach {
                        val parsed = it.mapAsync(::parseStopsOrder)
                            .mapNotNull { it }
                            .map {
                                val originalStop = repeatedToOriginalStops[it.fullStopCode]
                                if (originalStop != null) it.copy(fullStopCode = originalStop) else it
                            }
                        if (parsed.isNotEmpty()) stopsOrderCollectionNew.insertMany(parsed)
                    }
                }
                logger.info("Loaded bus stops order from gtfs")
            },
            async {
                logger.info("Loading calendars")
                gtfsReader.openAsync(getFromGtfs("calendar.txt", allGtfs)) {
                    val stops = readAllWithHeaderAsSequence().chunked(sequenceChunkSize)
                    stops.forEach {
                        val parsed = it.mapAsync(::parseCalendar).mapNotNull { it }
                        if (parsed.isNotEmpty()) calendarsCollectionNew.insertMany(parsed)
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
    } catch (ex: Throwable) {
        logger.error("Error loading data", ex)
        stopsCollectionNew.drop()
        stopsInfoCollectionNew.drop()
        itinerariesCollectionNew.drop()
        shapesCollectionNew.drop()
        stopsOrderCollectionNew.drop()
        calendarsCollectionNew.drop()
        routesCollectionNew.drop()
        return@withContext
    }

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

suspend fun getFromGtfs(file: String, routes: List<SuspendingLazy<String>>) = routes.mapIndexed { index, s ->
    if (index == 0) File("${s.value()}/$file").inputStream()
    else File("${s.value()}/$file").removeFirstLine().inputStream()
}.let { SequenceInputStream(it.toEnumeration()) }

suspend fun getFromFile(files: List<SuspendingLazy<String>>) = files.mapIndexed { index, s ->
    if (index == 0) File(s.value()).inputStream()
    else File(s.value()).removeFirstLine().inputStream()
}.let { SequenceInputStream(it.toEnumeration()) }
