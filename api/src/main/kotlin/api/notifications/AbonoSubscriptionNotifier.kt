package api.notifications

import api.db.getAbonoSubscriptions
import api.db.models.AbonoSubscription
import api.db.removeAbonoSubscription
import api.extensions.await
import api.extensions.batched
import api.extensions.forEachAsync
import api.routing.abono.getAbonoResponse
import com.google.firebase.ErrorCode
import com.google.firebase.messaging.*
import dev.inmo.krontab.doInfinityTz
import io.ktor.util.logging.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val logger = KtorSimpleLogger("AbonoSubscriptions")

@OptIn(DelicateCoroutinesApi::class)
fun notifyAbonosOnBackground() = GlobalScope.launch(Dispatchers.IO) {
    doInfinityTz("0 1 * * * 0o") {
        try {
            sendAbonoNotifications()
        } catch (e: Exception) {
            logger.error(e)
        }
    }
}

suspend fun sendAbonoNotifications() = getAbonoSubscriptions().batched(100).forEachAsync(::sendNotification)

private suspend fun sendNotification(abonoSubcription: AbonoSubscription) {
    val data = getAbonoResponse(abonoSubcription.ttp).getOrNull() ?: return
    data.contracts.forEach {
        if (it.leftDays == null || it.leftDays > 5) return@forEach
        val icon = "https://www.madridtransporte.com/favicon.ico"
        val title = "Abono apunto de caducar"
        val body =
            if (it.leftDays > 0) "Te quedan ${it.leftDays} para que tu abono ${abonoSubcription.abonoName} caduque"
            else "Tu abono ${abonoSubcription.abonoName} ha caducado"
        val tag = data.ttpNumber + it.contractName

        val message = Message.builder()
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setIcon(icon)
                            .setTag(tag)
                            .setTitle(title)
                            .setBody(body)
                            .build()
                    ).build()
            )
            .setWebpushConfig(
                WebpushConfig.builder()
                    .setNotification(
                        WebpushNotification.builder()
                            .setIcon(icon)
                            .setTag(tag)
                            .setTitle(title)
                            .setBody(body)
                            .setRenotify(true)
                            .build()
                    ).build()
            )
            .setToken(abonoSubcription.token.token)
            .build()

        try {
            logger.info("Sending abono notification message to ${abonoSubcription.token}")
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