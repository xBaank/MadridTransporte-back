import arrow.core.right
import busTrackerApi.routing.stops.Arrive
import busTrackerApi.routing.stops.Coordinates
import busTrackerApi.routing.stops.StopTimes
import busTrackerApi.routing.stops.getFunctionByCodMode
import busTrackerApi.startUp
import busTrackerApi.utils.StopTimesF
import com.google.api.core.ApiFuture
import com.google.firebase.ErrorCode
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.mockk.*
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import simpleJson.jObject
import simpleJson.serialized
import utils.MongoContainer
import utils.testApplicationBusTracker
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

enum class Subscriptions(val url: String, val stopCode: String) {
    BUS("/stops/bus/times", busStopCode),
    METRO("/stops/metro/times", metroStopCode),
    TRAIN("/stops/train/times", trainStopCode),
    EMT("/stops/emt/times", emtStopCode),
    TRAM("/stops/tram/times", tramStopCode)
}


class NotificationsTest {
    private val testStopTimes = StopTimes(
        codMode = 0,
        stopName = "",
        simpleStopCode = "",
        stopCode = "",
        coordinates = Coordinates(0.0, 0.0),
        arrives = listOf(
            Arrive(
                "",
                "",
                0,
                0,
                0,
                "",
                0
            )
        ),
        incidents = listOf()
    )

    private val delayTime = 5.seconds
    private val startupF: Application.() -> Unit = {
        MongoContainer.start()
        System.setProperty("NOTIFICATION_DELAY_TIME_SECONDS", delayTime.inWholeSeconds.toString())
        startUp()
    }

    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should subscribe to line times, get subscription and unsubscribe`(subscription: Subscriptions) =
        testApplicationBusTracker(startupF) {
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
                    "stopCode" += subscription.stopCode
                    "lineDestination" += jObject {
                        "line" += testStopTimes.arrives!!.first().line
                        "destination" += testStopTimes.arrives!!.first().destination
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
                    "stopCode" += subscription.stopCode
                }.serialized())
            }

            verify(timeout = 1.minutes.inWholeMilliseconds, atLeast = 1) { firebaseMessaging.sendAsync(any()) }

            val unsubscribeResponse = client.post(subscription.url + "/unsubscribe") {
                setBody(body)
            }

            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.isSuccess().shouldBe(true)
            unsubscribeResponse.status.isSuccess().shouldBe(true)
        }

    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should subscribe to line times and unsubscribe on error`(subscription: Subscriptions) =
        testApplicationBusTracker(startupF) {
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
                    "stopCode" += subscription.stopCode
                    "lineDestination" += jObject {
                        "line" += testStopTimes.arrives!!.first().line
                        "destination" += testStopTimes.arrives!!.first().destination
                        "codMode" += 8
                    }
                }
            }.serialized()

            val subscribedResponse = client.post(subscription.url + "/subscribe") {
                setBody(body)
            }

            verify(timeout = 1.minutes.inWholeMilliseconds, atLeast = 1) { firebaseMessaging.sendAsync(any()) }
            delay(delayTime.inWholeMilliseconds) //TODO We need this to wait for the subscription to be removed after sending the message failed

            val subscriptionsResponse = client.post(subscription.url + "/subscription") {
                setBody(jObject {
                    "deviceToken" += "token"
                    "stopCode" += subscription.stopCode
                }.serialized())
            }


            subscribedResponse.status.isSuccess().shouldBe(true)
            subscriptionsResponse.status.shouldBe(HttpStatusCode.NotFound)
        }

    @ParameterizedTest
    @EnumSource(Subscriptions::class)
    fun `should not subscribe to line times`(subscription: Subscriptions) =
        testApplicationBusTracker(startupF) {

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