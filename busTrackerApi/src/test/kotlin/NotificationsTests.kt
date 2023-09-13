import arrow.core.right
import busTrackerApi.routing.stops.*
import com.google.api.core.ApiFuture
import com.google.firebase.ErrorCode
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.*
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.jObject
import simpleJson.serialized
import utils.testApplicationBusTracker
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

enum class Subscriptions(val url: String, val stopcode: String) {
    BUS("/v1/stops/bus/times", busStopCode),
    METRO("/v1/stops/metro/times", metroStopCode),
    EMT("/v1/stops/emt/times", emtStopCode),
    TRAM("/v1/stops/tram/times", tramStopCode)
}

val testStopTimes = StopTimes(
    codMode = 0,
    stopName = "",
    simpleStopCode = "",
    stopCode = "",
    coordinates = Coordinates(0.0, 0.0),
    arrives = listOf(),
    incidents = listOf()
)

class NotificationsTest {
    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should subscribe to line times, get subscription and unsubscribe`(subscription: Subscriptions) =
        testApplicationBusTracker {
            delayTime = 5.seconds
            val functionMocked: StopTimesF = { testStopTimes.right() }
            val firebaseMessaging = mockk<FirebaseMessaging>()
            val future = mockk<ApiFuture<String>>()
            mockkStatic(FirebaseMessaging::class)
            mockkStatic(::getFunctionByCodMode)
            every { future.isDone } returns true
            every { future.get() } returns "MessageId"
            coEvery { getFunctionByCodMode(any()) } returns functionMocked.right()
            every { FirebaseMessaging.getInstance() } returns firebaseMessaging
            every { firebaseMessaging.sendAsync(any()) } returns future

            val body = jObject {
                "deviceToken" += "token"
                "subscription" += jObject {
                    "stopCode" += subscription.stopcode
                    "lineDestination" += jObject {
                        "line" += UUID.randomUUID().toString()
                        "destination" += UUID.randomUUID().toString()
                        "codMode" += 8
                    }
                }
            }.serialized()

            val subscribedResponse = client.post(subscription.url + "/subscribe") {
                setBody(body)
            }

            val subscriptionsResponse = client.post(subscription.url + "/subscription") {
                setBody(jObject {
                    "deviceToken" += "token"
                    "stopCode" += subscription.stopcode
                }.serialized())
            }

            delay(delayTime)

            val unsubscribeResponse = client.post(subscription.url + "/unsubscribe") {
                setBody(body)
            }

            verify(timeout = delayTime.toLong(DurationUnit.MILLISECONDS)) { firebaseMessaging.sendAsync(any()) }
            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.isSuccess().shouldBe(true)
            unsubscribeResponse.status.isSuccess().shouldBe(true)
        }

    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should subscribe to line times and unsubscribe on error`(subscription: Subscriptions) =
        testApplicationBusTracker {
            delayTime = 5.seconds
            val functionMocked: StopTimesF = { testStopTimes.right() }
            val firebaseMessaging = mockk<FirebaseMessaging>()
            val firebaseMessagingException = mockk<FirebaseMessagingException>()
            mockkStatic(FirebaseMessaging::class)
            mockkStatic(::getFunctionByCodMode)
            coEvery { getFunctionByCodMode(any()) } returns functionMocked.right()
            every { FirebaseMessaging.getInstance() } returns firebaseMessaging
            every { firebaseMessagingException.errorCode } answers { ErrorCode.NOT_FOUND }
            every { firebaseMessagingException.message } answers { "" }
            every { firebaseMessagingException.stackTrace } answers { emptyArray() }
            every { firebaseMessagingException.cause } answers { null }
            every { firebaseMessagingException.suppressed } answers { emptyArray() }
            every { firebaseMessaging.sendAsync(any()) } throws firebaseMessagingException

            val body = jObject {
                "deviceToken" += "token"
                "subscription" += jObject {
                    "stopCode" += subscription.stopcode
                    "lineDestination" += jObject {
                        "line" += UUID.randomUUID().toString()
                        "destination" += UUID.randomUUID().toString()
                        "codMode" += 8
                    }
                }
            }.serialized()

            val subscribedResponse = client.post(subscription.url + "/subscribe") {
                setBody(body)
            }

            delay(delayTime)

            val subscriptionsResponse = client.post(subscription.url + "/subscription") {
                setBody(jObject {
                    "deviceToken" += "token"
                    "stopCode" += subscription.stopcode
                }.serialized())
            }


            verify(timeout = delayTime.toLong(DurationUnit.MILLISECONDS)) { firebaseMessaging.sendAsync(any()) }
            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.shouldBe(HttpStatusCode.NotFound)
        }

    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should not subscribe to line times`(subscription: Subscriptions) =
        testApplicationBusTracker {
            delayTime = 5.seconds

            val body = jObject {
                "deviceToken" += "token"
                "subscription" += jObject {
                    "stopCode" += "88888"
                    "lineDestination" += jObject {
                        "line" += "8"
                        "destination" += "asd"
                        "codMode" += 8
                    }
                }
            }.serialized()

            val subscribedResponse = client.post(subscription.url + "/subscribe") {
                setBody(body)
            }

            subscribedResponse.status.shouldBe(HttpStatusCode.NotFound)

        }
}