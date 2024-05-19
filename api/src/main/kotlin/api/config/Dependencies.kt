package api.config

import arrow.core.raise.either
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
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


fun setupFirebase() = either {
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