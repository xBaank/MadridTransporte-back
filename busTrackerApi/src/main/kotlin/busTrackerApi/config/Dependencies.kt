package busTrackerApi.config

import arrow.core.continuations.either
import busTrackerApi.db.models.Itinerary
import busTrackerApi.db.models.Stop
import busTrackerApi.db.models.StopsInfo
import busTrackerApi.db.models.StopsSubscription
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.grpc.LoadBalancerRegistry
import io.grpc.internal.PickFirstLoadBalancerProvider
import okhttp3.OkHttpClient
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

val httpClient = OkHttpClient.Builder()
    .callTimeout(20.seconds.toJavaDuration())
    .connectTimeout(20.seconds.toJavaDuration())
    .readTimeout(20.seconds.toJavaDuration())
    .build()

private lateinit var db: MongoDatabase
val stopsCollection: MongoCollection<Stop> by lazy { db.getCollection("stops") }
val stopsInfoCollection: MongoCollection<StopsInfo> by lazy { db.getCollection("stopsInfo") }
val stopsSubscriptionsCollection: MongoCollection<StopsSubscription> by lazy { db.getCollection("stopsSubscriptions") }
val itinerariesCollection: MongoCollection<Itinerary> by lazy { db.getCollection("itineraries") }

suspend fun setupMongo() = either {
    db = MongoClient.create(EnvVariables.mongoConnectionString.bind()).getDatabase("busTracker")
}

suspend fun setupFirebase() = either {
    LoadBalancerRegistry.getDefaultRegistry().register(PickFirstLoadBalancerProvider())
    if (FirebaseApp.getApps().isNotEmpty()) return@either
    if (EnvVariables.serviceJson.isLeft()) {
        println("SERVICE_JSON not found, skipping firebase setup") //TODO add logging
        return@either
    }
    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(
            GoogleCredentials.fromStream(
                EnvVariables.serviceJson.bind().byteInputStream()
            )
        )
        .build()
    FirebaseApp.initializeApp(options)
}