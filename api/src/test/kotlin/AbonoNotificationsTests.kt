import api.notifications.sendAbonoNotifications
import api.routing.abono.Abono
import api.routing.abono.Contract
import api.routing.abono.getAbonoResponse
import arrow.core.right
import com.google.api.core.ApiFuture
import com.google.firebase.ErrorCode
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.*
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import simpleJson.jObject
import simpleJson.serialized
import utils.testApplicationBusTracker
import kotlin.time.Duration.Companion.minutes

class AbonoNotificationsTests {
    private val testAbono = Abono(
        serialNumber = "",
        ttpNumber = "",
        createdAt = "",
        expireAt = "",
        contracts = listOf(
            Contract(
                contractCode = 1,
                contractName = "",
                contractUserProfileType = "",
                contractUserProfilePropietaryCompany = "",
                contractCompanyPropietary = 1,
                charges = 1,
                chargeDate = "",
                remainingCharges = 1,
                firstUseDate = "",
                firstUseDateLimit = "",
                lastUseDate = "",
                leftDays = 1,
                useDays = 1
            )
        )
    )

    @Test
    fun `should subscribe to abono, get subscription and unsubscribe`() =
        testApplicationBusTracker {
            val firebaseMessaging = mockk<FirebaseMessaging>()
            val future = mockk<ApiFuture<String>>()
            mockkStatic(FirebaseMessaging::class)
            mockkStatic(::getAbonoResponse)
            every { future.isDone } returns true
            every { future.get() } returns "MessageId"
            coEvery { getAbonoResponse(any()) } returns testAbono.right()
            every { FirebaseMessaging.getInstance() } returns firebaseMessaging
            every { firebaseMessaging.sendAsync(any()) } returns future

            val body = jObject {
                "deviceToken" += "token"
                "ttpNumber" += testAbono.ttpNumber
                "name" += "test"
            }.serialized()

            val subscribedResponse = client.post("/abono/subscribe") {
                setBody(body)
            }

            val subscriptionsResponse = client.post("/abono/subscription") {
                setBody(jObject {
                    "deviceToken" += "token"
                    "ttpNumber" += testAbono.ttpNumber
                    "name" += "test"
                }.serialized())
            }

            sendAbonoNotifications()

            verify(timeout = 1.minutes.inWholeMilliseconds, atLeast = 1) { firebaseMessaging.sendAsync(any()) }

            val unsubscribeResponse = client.post("/abono/unsubscribe") {
                setBody(body)
            }

            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.isSuccess().shouldBe(true)
            unsubscribeResponse.status.isSuccess().shouldBe(true)
        }

    @Test
    fun `should subscribe to abono and unsubscribe on error`() =
        testApplicationBusTracker {
            val firebaseMessaging = mockk<FirebaseMessaging>()
            val firebaseMessagingException = mockk<FirebaseMessagingException>()
            mockkStatic(FirebaseMessaging::class)
            mockkStatic(::getAbonoResponse)
            coEvery { getAbonoResponse(any()) } returns testAbono.right()
            every { FirebaseMessaging.getInstance() } returns firebaseMessaging
            every { firebaseMessagingException.errorCode } answers { ErrorCode.NOT_FOUND }
            every { firebaseMessagingException.message } answers { "" }
            every { firebaseMessagingException.stackTrace } answers { emptyArray() }
            every { firebaseMessagingException.cause } answers { null }
            every { firebaseMessagingException.suppressed } answers { emptyArray() }
            every { firebaseMessaging.sendAsync(any()) } throws firebaseMessagingException

            val body = jObject {
                "deviceToken" += "token"
                "ttpNumber" += testAbono.ttpNumber
                "name" += "test"
            }.serialized()

            val subscribedResponse = client.post("/abono/subscribe") {
                setBody(body)
            }

            sendAbonoNotifications()

            verify(timeout = 1.minutes.inWholeMilliseconds, atLeast = 1) { firebaseMessaging.sendAsync(any()) }

            val subscriptionsResponse = client.post("/abono/subscription") {
                setBody(jObject {
                    "deviceToken" += "token"
                    "ttpNumber" += testAbono.ttpNumber
                    "name" += "test"
                }.serialized())
            }


            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.shouldBe(HttpStatusCode.NotFound)
        }

    @Test
    fun `should not subscribe to abono`() = testApplicationBusTracker {
        val body = jObject {
            "deviceToken" += "token"
            "ttpNumber" += "asd"
            "name" += "test"
        }.serialized()

        val subscribedResponse = client.post("/abono/subscribe") {
            setBody(body)
        }

        subscribedResponse.status.shouldBe(HttpStatusCode.NotFound)
    }

    @AfterEach
    fun cleanup(): Unit = unmockkStatic(::getAbonoResponse)

}