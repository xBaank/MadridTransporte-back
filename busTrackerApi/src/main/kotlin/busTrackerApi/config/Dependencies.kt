package busTrackerApi.config

import arrow.core.continuations.either
import busTrackerApi.db.Stop
import busTrackerApi.db.StopsInfo
import busTrackerApi.utils.getenvWrapped
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

fun setupMongo() = either.eager {
    db = MongoClient.create(getenvWrapped("MONGO_CONNECTION_STRING").bind()).getDatabase("busTracker")
}

fun setupFirebase() = either.eager {
    //Fix bug `pick_first` in jar
    LoadBalancerRegistry.getDefaultRegistry().register(PickFirstLoadBalancerProvider())
    if (FirebaseApp.getApps().isNotEmpty()) return@eager
    if (getenvWrapped("SERVICE_JSON").isLeft()) {
        println("SERVICE_JSON not found, skipping firebase setup") //TODO add logging
        return@eager
    }
    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(
            GoogleCredentials.fromStream(
                getenvWrapped("SERVICE_JSON").bind().byteInputStream()
            )
        )
        .build()
    FirebaseApp.initializeApp(options)
}