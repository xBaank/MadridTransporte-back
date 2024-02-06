package api.notifications

import api.db.getAbonoSubscriptions
import api.db.removeAbonoSubscription
import api.extensions.await
import api.extensions.batched
import api.extensions.forEachAsync
import api.routing.abono.getAbonoResponse
import com.google.firebase.ErrorCode
import com.google.firebase.messaging.*
import io.ktor.util.logging.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.days

private val logger = KtorSimpleLogger("AbonoSubscriptions")

@OptIn(DelicateCoroutinesApi::class)
fun notifyAbonosOnBackground() = GlobalScope.launch(Dispatchers.IO) {
    while (isActive) {
        getAbonoSubscriptions().batched(100).forEachAsync { abonoSubcription ->
            val data = getAbonoResponse(abonoSubcription.ttp).getOrNull() ?: return@forEachAsync
            data.contracts.forEach {
                if (it.leftDays == null || it.leftDays > 5) return@forEach
                val icon = "https://www.madridtransporte.com/favicon.ico"

                val message = Message.builder()
                    .setAndroidConfig(
                        AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(
                                AndroidNotification.builder()
                                    .setIcon(icon)
                                    .setTag(data.ttpNumber + it.contractName)
                                    .setTitle("Abono apunto de caducar")
                                    .setBody("Te quedan ${it.leftDays} para que tu abono caduque")
                                    .build()
                            ).build()
                    )
                    .setWebpushConfig(
                        WebpushConfig.builder()
                            .putHeader("Urgency", "high")
                            .setNotification(
                                WebpushNotification.builder()
                                    .setIcon(icon)
                                    .setTag(data.ttpNumber + it.contractName)
                                    .setTitle("Abono apunto de caducar")
                                    .setBody("Te quedan ${it.leftDays} para que tu abono caduque")
                                    .setRenotify(true)
                                    .build()
                            ).build()
                    )
                    .setToken(abonoSubcription.token.token)
                    .build()

                try {
                    logger.info("Sending message to $it")
                    FirebaseMessaging.getInstance().sendAsync(message).await()
                } catch (e: FirebaseMessagingException) {
                    logger.error(e)
                    if (e.errorCode == ErrorCode.INVALID_ARGUMENT || e.errorCode == ErrorCode.NOT_FOUND || e.messagingErrorCode == MessagingErrorCode.UNREGISTERED) {
                        removeAbonoSubscription(abonoSubcription)
                    }
                } catch (e: Exception) {
                    logger.error(e)
                }
            }
        }
        delay(1.days)
    }
}