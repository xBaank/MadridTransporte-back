package busTrackerApi.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import okhttp3.OkHttpClient
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

val httpClient = OkHttpClient.Builder()
    .callTimeout(20.seconds.toJavaDuration())
    .connectTimeout(20.seconds.toJavaDuration())
    .readTimeout(20.seconds.toJavaDuration())
    .build()

fun setupFirebase() {
    if (FirebaseApp.getApps().isNotEmpty()) return
    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()

    FirebaseApp.initializeApp(options)
}