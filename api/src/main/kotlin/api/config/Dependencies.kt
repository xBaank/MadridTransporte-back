package api.config

import api.db.models.*
import arrow.core.continuations.either
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
val stopsCollection: MongoCollection<Stop> by lazy { db.getCollection(Stop::class.simpleName!!) }
val stopsInfoCollection: MongoCollection<StopInfo> by lazy { db.getCollection(StopInfo::class.simpleName!!) }
val stopsSubscriptionsCollection: MongoCollection<StopsSubscription> by lazy { db.getCollection(StopsSubscription::class.simpleName!!) }
val itinerariesCollection: MongoCollection<Itinerary> by lazy { db.getCollection(Itinerary::class.simpleName!!) }
val shapesCollection: MongoCollection<Shape> by lazy { db.getCollection(Shape::class.simpleName!!) }
val stopsOrderCollection: MongoCollection<StopOrder> by lazy { db.getCollection(StopOrder::class.simpleName!!) }
val routesCollection: MongoCollection<Route> by lazy { db.getCollection(Route::class.simpleName!!) }

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