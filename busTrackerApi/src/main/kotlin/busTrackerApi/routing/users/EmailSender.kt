package busTrackerApi.routing.users

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.simplejavamail.email.EmailBuilder

fun sendAccountVerification(user : User, verifyUrl : String) {
    val email = EmailBuilder.startingBlank()
        .from("BusTracker", "noreply@bustracker.com")
        .to(user.username, user.email)
        .withSubject("Account Verification")
        .withHTMLText(createVerifyTemplate(user.username, verifyUrl))
        .buildEmail()

    CoroutineScope(Dispatchers.IO).launch { mailer.sendMail(email) }
}

fun sendResetPassword(user : User, redirectUrl : String) {
    val emailToSend = EmailBuilder.startingBlank()
        .from("BusTracker", "noreply@bustracker.com")
        .to(user.username, user.email)
        .withSubject("Reset Password")
        .withHTMLText(createResetPasswordEmailTemplate(user.username, redirectUrl))
        .buildEmail()

    CoroutineScope(Dispatchers.IO).launch { mailer.sendMail(emailToSend) }
}